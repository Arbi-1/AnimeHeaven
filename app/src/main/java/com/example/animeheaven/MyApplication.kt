package com.example.animeheaven

import android.app.Application
import com.google.firebase.FirebaseApp
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase
       FirebaseApp.initializeApp(this)

        // Initialize Facebook SDK (new recommended way)
        //FacebookSdk.fullyInitialize()
        //AppEventsLogger.activateApp(this)
    }
}
