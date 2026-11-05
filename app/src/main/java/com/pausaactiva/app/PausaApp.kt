package com.pausaactiva.app

import android.app.Application
import com.pausaactiva.app.notify.Notifier

class PausaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Notifier.ensureChannel(this)
    }
}
