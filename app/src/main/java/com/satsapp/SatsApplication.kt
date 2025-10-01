package com.satsapp

import android.app.Application

/**
 * Application class for SatsApp
 * Initialize app-wide dependencies and configurations here
 */
class SatsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Application-level initialization
        // CDK-Kotlin library will be initialized when wallet is created
    }
}
