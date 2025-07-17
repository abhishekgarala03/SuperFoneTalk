package app.abhishekgarala.superfone.domain.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.abhishekgarala.superfone.domain.repo.AuthRepository
import app.abhishekgarala.superfone.presentation.state.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.observeAuthState().collect { state ->
                _authState.value = state
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            authRepository.signInWithGoogle(idToken).fold(
                onSuccess = { user ->
                    _authState.value = AuthState.Authenticated(user)
                },
                onFailure = { error ->
                    _authState.value = AuthState.Error(error.message ?: "Sign in failed")
                }
            )
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut().fold(
                onSuccess = {
                    _authState.value = AuthState.Unauthenticated
                },
                onFailure = { error ->
                    _authState.value = AuthState.Error(error.message ?: "Sign out failed")
                }
            )
        }
    }
}