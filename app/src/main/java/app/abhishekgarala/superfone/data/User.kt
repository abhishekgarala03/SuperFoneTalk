package app.abhishekgarala.superfone.data

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val isOnline: Boolean = false,
    val isSearching: Boolean = false,
    val currentCallId: String? = null
)