package com.example

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class ShopWaveApp : Application() {

    override fun onCreate() {
        super.onCreate()
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                val initialized = FirebaseApp.initializeApp(this)
                if (initialized == null) {
                    val options = FirebaseOptions.Builder()
                        .setApplicationId("1:365937247800:android:shopwave")
                        .setApiKey("AIzaSyDummyKeyForShopWaveBuildEnvSafe123")
                        .setProjectId("shopwave-ecom")
                        .build()
                    FirebaseApp.initializeApp(this, options)
                }
            }
            Log.d("ShopWaveApp", "FirebaseApp initialized successfully")
        } catch (e: Exception) {
            Log.w("ShopWaveApp", "Firebase initialization fallback: ${e.message}")
            try {
                val options = FirebaseOptions.Builder()
                    .setApplicationId("1:365937247800:android:shopwave")
                    .setApiKey("AIzaSyDummyKeyForShopWaveBuildEnvSafe123")
                    .setProjectId("shopwave-ecom")
                    .build()
                FirebaseApp.initializeApp(this, options)
            } catch (e2: Exception) {
                Log.e("ShopWaveApp", "Secondary Firebase init exception: ${e2.message}")
            }
        }
    }
}
