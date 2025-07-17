package app.abhishekgarala.superfone.data

data class CallSession(
    val callId: String = "",
    val user1: User = User(),
    val user2: User = User(),
    val status: CallStatus = CallStatus.WAITING,
    val startTime: Long = 0,
    val endTime: Long = 0
)
