package app.abhishekgarala.superfone.init

import app.abhishekgarala.superfone.data.User
import com.google.firebase.auth.FirebaseAuth

fun getCurrentUser(): User? {
    return FirebaseAuth.getInstance().currentUser?.let { firebaseUser ->
        User(
            uid = firebaseUser.uid,
            name = firebaseUser.displayName ?: "",
            email = firebaseUser.email ?: "",
            photoUrl = firebaseUser.photoUrl?.toString() ?: ""
        )
    }
}

fun formatCallDuration(duration: Long): String {
    val seconds = (duration / 1000) % 60
    val minutes = (duration / (1000 * 60)) % 60
    val hours = (duration / (1000 * 60 * 60)) % 24

    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}

fun getCurrentUserId(): String {
    val currentUser = FirebaseAuth.getInstance().currentUser
    return currentUser?.uid.toString()
}
