package com.example.resumebuilder

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ResumeBuilderApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize things here if needed
    }
}