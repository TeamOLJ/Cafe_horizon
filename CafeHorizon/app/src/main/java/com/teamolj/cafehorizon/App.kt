package com.teamolj.cafehorizon
import android.app.Application

class App: Application() {
    companion object {
        lateinit var prefs: AppSharedPreferences
    }

    override fun onCreate() {
        prefs = AppSharedPreferences(applicationContext)
        super.onCreate()
    }
}