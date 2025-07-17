package app.abhishekgarala.superfone.presentation.state

import app.abhishekgarala.superfone.data.User

sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}