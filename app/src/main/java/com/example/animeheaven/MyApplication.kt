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

        // Initialize Facebook SDK
        FacebookSdk.setApplicationId("1274607884024604") // optional, but ensures correct App ID
        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)
    }
}
