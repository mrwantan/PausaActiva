package com.pausaactiva.app.notify

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.pausaactiva.app.data.Prefs
import com.pausaactiva.app.data.Schedule

/**
 * Programa UNA alarma exacta con el próximo aviso. Cuando suena, AlarmReceiver muestra la notificación
 * y programa la siguiente. Todo es local: no hace falta internet ni servidor.
 */
object AlarmScheduler {
    const val ACTION_ALARM = "com.pausaactiva.app.ALARM"
    const val ACTION_TEST = "com.pausaactiva.app.ALARM_TEST"
    private const val REQ_ALARM = 77
    private const val REQ_TEST = 78
    private const val SNOOZE_MS = 5 * 60_000L

    private fun alarmManager(ctx: Context) = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private fun pending(ctx: Context, req: Int, action: String): PendingIntent =
        PendingIntent.getBroadcast(
            ctx, req, Intent(ctx, AlarmReceiver::class.java).setAction(action),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

    fun canExact(ctx: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager(ctx).canScheduleExactAlarms()

    private fun set(ctx: Context, at: Long, pi: PendingIntent) {
        val am = alarmManager(ctx)
        try {
            if (canExact(ctx)) am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pi)
            else am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pi) // sin permiso de alarma exacta: puede retrasarse unos minutos
        } catch (e: SecurityException) {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pi)
        }
    }

    fun scheduleAt(ctx: Context, at: Long) {
        val pi = pending(ctx, REQ_ALARM, ACTION_ALARM)
        alarmManager(ctx).cancel(pi)
        set(ctx, at, pi)
    }

    fun cancel(ctx: Context) {
        alarmManager(ctx).cancel(pending(ctx, REQ_ALARM, ACTION_ALARM))
    }

    /** Aviso de prueba de punta a punta: sirve para comprobar que llega con la app cerrada. */
    fun testIn(ctx: Context, delayMs: Long) {
        set(ctx, System.currentTimeMillis() + delayMs, pending(ctx, REQ_TEST, ACTION_TEST))
    }

    fun enable(ctx: Context) {
        val p = Prefs.get(ctx)
        val now = System.currentTimeMillis()
        p.enabled = true
        p.snoozeActive = false
        p.lastAck = now
        p.nextDue = Schedule.nextValid(now + p.interval * 60_000L, p.settings())
        scheduleAt(ctx, p.nextDue)
    }

    fun disable(ctx: Context) {
        Prefs.get(ctx).enabled = false
        Prefs.get(ctx).snoozeActive = false
        cancel(ctx)
        Notifier.cancel(ctx)
    }

    fun snooze(ctx: Context) {
        val p = Prefs.get(ctx)
        val now = System.currentTimeMillis()
        p.enabled = true
        p.snoozeActive = true
        p.lastAck = now
        p.nextDue = now + SNOOZE_MS
        scheduleAt(ctx, p.nextDue)
        Notifier.cancel(ctx)
    }

    /** Recalcula el próximo aviso a partir de ahora (tras cambiar ajustes o terminar una pausa). */
    fun reschedule(ctx: Context) {
        val p = Prefs.get(ctx)
        if (!p.enabled) return
        val now = System.currentTimeMillis()
        p.snoozeActive = false
        p.nextDue = Schedule.nextValid(now + p.interval * 60_000L, p.settings())
        scheduleAt(ctx, p.nextDue)
    }

    /** Vuelve a armar la alarma desde el estado guardado (arranque del teléfono, abrir la app, cambio de hora…). */
    fun rearm(ctx: Context) {
        val p = Prefs.get(ctx)
        if (!p.enabled) {
            cancel(ctx)
            return
        }
        val now = System.currentTimeMillis()
        val s = p.settings()
        var at = p.nextDue
        if (at <= now) {
            if (Schedule.inWindow(now, s)) {
                at = now + 3_000L // se perdió un aviso (teléfono apagado): avisa enseguida
            } else {
                at = Schedule.nextValid(now, s)
                p.nextDue = at
            }
        }
        scheduleAt(ctx, at)
    }
}
