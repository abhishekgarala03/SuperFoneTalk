package app.abhishekgarala.superfone

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SuperFoneApp : Application(){

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
    }

}