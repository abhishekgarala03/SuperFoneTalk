package app.abhishekgarala.superfone.permissions

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun rememberPermissionStatus(): Boolean? {
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.INTERNET
        )
    )

    LaunchedEffect(Unit) {
        permissionsState.launchMultiplePermissionRequest()
    }

    return when {
        permissionsState.allPermissionsGranted -> true
        permissionsState.shouldShowRationale -> false
        else -> null
    }
}
