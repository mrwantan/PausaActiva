package com.pausaactiva.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.pausaactiva.app.notify.AlarmScheduler
import com.pausaactiva.app.notify.Notifier
import com.pausaactiva.app.ui.AppViewModel
import com.pausaactiva.app.ui.PausaAppUi
import com.pausaactiva.app.ui.PausaTheme
import com.pausaactiva.app.ui.SystemActions

class MainActivity : ComponentActivity() {
    private val vm: AppViewModel by viewModels()
    private val actions = SystemActions(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PausaTheme {
                PausaAppUi(vm, actions)
            }
        }
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        // Reafirma la alarma por si el sistema la perdió, y refresca el estado de los permisos.
        AlarmScheduler.rearm(this)
        vm.refresh()
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.getStringExtra(Notifier.EXTRA_ACTION) == Notifier.ACTION_START) {
            intent.removeExtra(Notifier.EXTRA_ACTION)
            vm.startSession()
        }
    }
}
