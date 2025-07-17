package app.abhishekgarala.superfone.domain.repo

import app.abhishekgarala.superfone.data.CallSession
import app.abhishekgarala.superfone.data.CallStatus
import app.abhishekgarala.superfone.data.User
import app.abhishekgarala.superfone.presentation.state.CallState
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallRepository @Inject constructor(
    private val database: FirebaseDatabase,
    private val authRepository: AuthRepository
) {

    suspend fun startSearching(): Result<Unit> {
        return try {
            val currentUser =
                authRepository.getCurrentUser() ?: throw Exception("User not logged in")

            database.reference.child("users").child(currentUser.uid)
                .child("isSearching").setValue(true).await()

            val searchingUsers = database.reference.child("users")
                .orderByChild("isSearching").equalTo(true).get().await()

            val availableUsers = searchingUsers.children.mapNotNull { snapshot ->
                snapshot.getValue(User::class.java)
            }.filter { it.uid != currentUser.uid && it.currentCallId == null }

            if (availableUsers.isNotEmpty()) {
                val partner = availableUsers.first()
                val callId = database.reference.child("calls").push().key
                    ?: throw Exception("Failed to generate call ID")

                val callSession = CallSession(
                    callId = callId,
                    user1 = currentUser,
                    user2 = partner,
                    status = CallStatus.ACTIVE,
                    startTime = System.currentTimeMillis()
                )

                database.reference.child("calls").child(callId).setValue(callSession).await()

                database.reference.child("users").child(currentUser.uid).updateChildren(
                    mapOf(
                        "isSearching" to false,
                        "currentCallId" to callId
                    )
                ).await()

                database.reference.child("users").child(partner.uid).updateChildren(
                    mapOf(
                        "isSearching" to false,
                        "currentCallId" to callId
                    )
                ).await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun stopSearching(): Result<Unit> {
        return try {
            val currentUser =
                authRepository.getCurrentUser() ?: throw Exception("User not logged in")

            database.reference.child("users").child(currentUser.uid)
                .child("isSearching").setValue(false).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun endCall(callId: String): Result<Unit> {
        return try {
            val currentUser =
                authRepository.getCurrentUser() ?: throw Exception("User not logged in")

            database.reference.child("calls").child(callId).updateChildren(
                mapOf(
                    "status" to CallStatus.ENDED.name,
                    "endTime" to System.currentTimeMillis()
                )
            ).await()

            database.reference.child("users").child(currentUser.uid)
                .child("currentCallId").removeValue().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeCallState(): Flow<CallState> = callbackFlow {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser == null) {
            trySend(CallState.Error("User not logged in"))
            close()
            return@callbackFlow
        }

        val userRef = database.reference.child("users").child(currentUser.uid)
        val callListeners = mutableListOf<Pair<DatabaseReference, ValueEventListener>>()

        val userListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    when {
                        user.isSearching -> trySend(CallState.Searching)
                        user.currentCallId != null -> {
                            val callRef =
                                database.reference.child("calls").child(user.currentCallId)
                            val callListener = object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val callSession = snapshot.getValue(CallSession::class.java)
                                    if (callSession != null) {
                                        when (callSession.status) {
                                            CallStatus.ACTIVE -> trySend(
                                                CallState.InCall(
                                                    callSession
                                                )
                                            )

                                            CallStatus.ENDED -> trySend(CallState.Idle)
                                            else -> trySend(CallState.Found(callSession))
                                        }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    trySend(CallState.Error(error.message))
                                }
                            }
                            callRef.addValueEventListener(callListener)
                            callListeners.add(Pair(callRef, callListener))
                        }

                        else -> trySend(CallState.Idle)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(CallState.Error(error.message))
            }
        }

        userRef.addValueEventListener(userListener)

        awaitClose {
            userRef.removeEventListener(userListener)
            callListeners.forEach { (ref, listener) ->
                ref.removeEventListener(listener)
            }
        }
    }


    private fun observeCallSession(callId: String) = callbackFlow {

        database.reference.child("calls").child(callId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val callSession = snapshot.getValue(CallSession::class.java)
                    if (callSession != null) {
                        when (callSession.status) {
                            CallStatus.ACTIVE -> trySend(CallState.InCall(callSession))
                            CallStatus.ENDED -> trySend(CallState.Idle)
                            else -> trySend(CallState.Found(callSession))
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(CallState.Error(error.message))
                }
            })
    }
}