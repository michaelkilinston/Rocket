package com.launchrocket.playsgame.app.views.applications

import android.app.Application
import com.onesignal.OneSignal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainApplication: Application() {
    private fun getOneSignalId() = "58dba52a-07be-41a1-89c7-2196c7ee7490"
    override fun onCreate() {
        super.onCreate()

        OneSignal.initWithContext(this, getOneSignalId())
        CoroutineScope(Dispatchers.IO).launch {
            OneSignal.Notifications.requestPermission(true)
        }
    }
}