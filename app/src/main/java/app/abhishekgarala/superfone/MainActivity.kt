package app.abhishekgarala.superfone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import app.abhishekgarala.superfone.domain.viewmodel.AuthViewModel
import app.abhishekgarala.superfone.domain.viewmodel.CallViewModel
import app.abhishekgarala.superfone.init.StartApp
import app.abhishekgarala.superfone.ui.theme.SuperFoneTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private val callViewModel: CallViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SuperFoneTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StartApp(authViewModel, callViewModel)
                }
            }
        }
    }
}
