package app.abhishekgarala.superfone.init

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import app.abhishekgarala.superfone.domain.viewmodel.AuthViewModel
import app.abhishekgarala.superfone.domain.viewmodel.CallViewModel
import app.abhishekgarala.superfone.googlesignin.rememberGoogleSignInHelper
import app.abhishekgarala.superfone.jitsimeet.JitsiMeetHelper
import app.abhishekgarala.superfone.permissions.rememberPermissionStatus
import app.abhishekgarala.superfone.presentation.state.AuthState
import app.abhishekgarala.superfone.presentation.state.CallState
import app.abhishekgarala.superfone.presentation.view.LoginScreen
import app.abhishekgarala.superfone.presentation.view.CallScreen
import app.abhishekgarala.superfone.presentation.view.HomeScreen
import app.abhishekgarala.superfone.ui.ErrorScreen
import app.abhishekgarala.superfone.ui.LoadingScreen
import app.abhishekgarala.superfone.ui.PermissionDeniedScreen
import kotlinx.coroutines.launch

@Composable
fun StartApp(
    authViewModel: AuthViewModel,
    callViewModel: CallViewModel
) {

    val googleSignInHelper = rememberGoogleSignInHelper()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val authState by authViewModel.authState.collectAsState()
    val callState by callViewModel.callState.collectAsState()
    val callDuration by callViewModel.callDuration.collectAsState()

    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val idToken = googleSignInHelper.handleSignInResult(result.data)
            if (idToken != null) {
                authViewModel.signInWithGoogle(idToken)
            }
        }
    }

    LaunchedEffect(callState) {
        if (callState is CallState.Found) {
            val user = (authState as? AuthState.Authenticated)?.user
            if (user != null) {
                JitsiMeetHelper.startCall(
                    context = context,
                    callId = (callState as CallState.Found).callSession.callId,
                    userName = user.name,
                    userEmail = user.email,
                    userAvatar = user.photoUrl
                )
            }
        }
    }

    val permissionGranted = rememberPermissionStatus()

    when (val currentAuthState = authState) {
        is AuthState.Loading -> {
            LoadingScreen()
        }

        is AuthState.Unauthenticated -> {
            LoginScreen(
                onGoogleSignIn = {
                    scope.launch {
                        val signInIntent = googleSignInHelper.getSignInIntent()
                        signInLauncher.launch(signInIntent)
                    }
                },
                isLoading = false
            )
        }

        is AuthState.Authenticated -> {
            when (permissionGranted) {
                true -> {
                    when (val currentCallState = callState) {
                        is CallState.InCall -> {
                            CallScreen(
                                callSession = currentCallState.callSession,
                                callDuration = callDuration,
                                onEndCall = { callViewModel.endCall(currentCallState.callSession.callId) }
                            )
                        }

                        else -> {
                            HomeScreen(
                                user = currentAuthState.user,
                                callState = currentCallState,
                                onFindStranger = { callViewModel.startSearching() },
                                onStopSearching = { callViewModel.stopSearching() },
                                onSignOut = { authViewModel.signOut() }
                            )
                        }
                    }
                }

                false -> {
                    PermissionDeniedScreen()
                }

                null -> {
                    LoadingScreen()
                }
            }
        }

        is AuthState.Error -> {
            ErrorScreen(
                message = currentAuthState.message,
                onRetry = { authViewModel.signOut() }
            )
        }
    }
}

