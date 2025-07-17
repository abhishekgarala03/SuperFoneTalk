package app.abhishekgarala.superfone.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.abhishekgarala.superfone.data.CallSession
import app.abhishekgarala.superfone.init.formatCallDuration
import app.abhishekgarala.superfone.init.getCurrentUserId
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun CallScreen(
    callSession: CallSession,
    callDuration: Long,
    onEndCall: () -> Unit
) {
    val partner =
        if (callSession.user1.uid == getCurrentUserId()) callSession.user2 else callSession.user1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Partner Info
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(partner.photoUrl)
                    .build(),
                contentDescription = "Partner Profile",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = partner.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = formatCallDuration(callDuration),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            FloatingActionButton(
                onClick = { /* TODO: Implement mute */ },
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_btn_speak_now),
                    contentDescription = "Mute"
                )
            }

            FloatingActionButton(
                onClick = onEndCall,
                containerColor = MaterialTheme.colorScheme.error
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_call),
                    contentDescription = "End Call"
                )
            }

            FloatingActionButton(
                onClick = { /* TODO: Implement speaker */ },
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_lock_silent_mode_off),
                    contentDescription = "Speaker"
                )
            }
        }
    }
}