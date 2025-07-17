package app.abhishekgarala.superfone.jitsimeet

import android.content.Context
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetUserInfo
import java.net.URL

object JitsiMeetHelper {

    fun startCall(
        context: Context,
        callId: String,
        userName: String,
        userEmail: String,
        userAvatar: String? = null
    ) {
        try {
            val userInfo = JitsiMeetUserInfo().apply {
                displayName = userName
                email = userEmail
                avatar = userAvatar?.let { URL(it) }
            }

            val options = JitsiMeetConferenceOptions.Builder()
                .setRoom(callId)
                .setServerURL(URL("https://meet.jit.si"))
                .setUserInfo(userInfo)
                .setAudioMuted(false)
                .setVideoMuted(true)
                .setAudioOnly(true)
                .build()

            JitsiMeetActivity.launch(context, options)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}