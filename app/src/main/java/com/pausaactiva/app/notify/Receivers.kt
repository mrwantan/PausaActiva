package com.pausaactiva.app.notify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.pausaactiva.app.data.Prefs
import com.pausaactiva.app.data.Schedule

/** Suena la alarma: muestra la notificación y deja armada la siguiente. */
class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            AlarmScheduler.ACTION_TEST -> Notifier.showTest(context)
            AlarmScheduler.ACTION_ALARM -> {
                val p = Prefs.get(context)
                if (!p.enabled) return
                val now = System.currentTimeMillis()
                val s = p.settings()
                // Un aviso pospuesto suena aunque caiga justo fuera del horario.
                if (p.snoozeActive || Schedule.inWindow(now, s)) {
                    Notifier.showBreak(context)
                    p.lastFire = now
                }
                p.snoozeActive = false
                p.nextDue = Schedule.nextValid(now + s.interval * 60_000L, s)
                AlarmScheduler.scheduleAt(context, p.nextDue)
            }
        }
    }
}

/** Botón «Posponer 5 min» de la notificación. */
class ActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Notifier.ACTION_SNOOZE) AlarmScheduler.snooze(context)
    }
}

/** Reinicio del teléfono, actualización de la app, cambio de hora o de permiso de alarmas exactas. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Notifier.ensureChannel(context)
        AlarmScheduler.rearm(context)
    }
}
