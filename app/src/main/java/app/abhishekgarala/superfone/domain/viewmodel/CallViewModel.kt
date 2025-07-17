package app.abhishekgarala.superfone.domain.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.abhishekgarala.superfone.domain.repo.CallRepository
import app.abhishekgarala.superfone.presentation.state.CallState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CallViewModel @Inject constructor(
    private val callRepository: CallRepository
) : ViewModel() {

    private val _callState = MutableStateFlow<CallState>(CallState.Idle)
    val callState: StateFlow<CallState> = _callState.asStateFlow()

    private val _callDuration = MutableStateFlow(0L)
    val callDuration: StateFlow<Long> = _callDuration.asStateFlow()

    init {
        viewModelScope.launch {
            callRepository.observeCallState().collect { state ->
                _callState.value = state

                if (state is CallState.InCall) {
                    startCallTimer(state.callSession.startTime)
                }
            }
        }
    }

    fun startSearching() {
        viewModelScope.launch {
            _callState.value = CallState.Searching

            callRepository.startSearching().fold(
                onSuccess = { },
                onFailure = { error ->
                    _callState.value = CallState.Error(error.message ?: "Failed to start searching")
                }
            )
        }
    }

    fun stopSearching() {
        viewModelScope.launch {
            callRepository.stopSearching().fold(
                onSuccess = {
                    _callState.value = CallState.Idle
                },
                onFailure = { error ->
                    _callState.value = CallState.Error(error.message ?: "Failed to stop searching")
                }
            )
        }
    }

    fun endCall(callId: String) {
        viewModelScope.launch {
            callRepository.endCall(callId).fold(
                onSuccess = {
                    _callState.value = CallState.Idle
                    _callDuration.value = 0L
                },
                onFailure = { error ->
                    _callState.value = CallState.Error(error.message ?: "Failed to end call")
                }
            )
        }
    }

    private fun startCallTimer(startTime: Long) {
        viewModelScope.launch {
            while (_callState.value is CallState.InCall) {
                _callDuration.value = System.currentTimeMillis() - startTime
                kotlinx.coroutines.delay(1000)
            }
        }
    }
}