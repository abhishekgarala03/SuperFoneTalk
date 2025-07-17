package app.abhishekgarala.superfone.presentation.state

import app.abhishekgarala.superfone.data.CallSession

sealed class CallState {
    object Idle : CallState()
    object Searching : CallState()
    data class Found(val callSession: CallSession) : CallState()
    data class InCall(val callSession: CallSession) : CallState()
    data class Error(val message: String) : CallState()
}