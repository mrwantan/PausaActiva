package com.pausaactiva.app.notify

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.pausaactiva.app.MainActivity
import com.pausaactiva.app.R

object Notifier {
    /** Si cambias la configuración del canal hay que cambiar el id (Android no deja modificarlo después de crearlo). */
    const val CHANNEL_ID = "pausa_avisos_v1"
    const val NOTIF_ID = 1001
    const val ACTION_SNOOZE = "com.pausaactiva.app.SNOOZE"
    const val EXTRA_ACTION = "pa_action"
    const val ACTION_START = "start"

    fun ensureChannel(ctx: Context) {
        val nm = ctx.getSystemService(NotificationManager::class.java) ?: return
        if (nm.getNotificationChannel(CHANNEL_ID) != null) return
        val channel = NotificationChannel(CHANNEL_ID, "Avisos de pausa activa", NotificationManager.IMPORTANCE_HIGH).apply {
            description = "Te avisa cuando es hora de tu pausa activa."
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 300, 120, 300, 120, 600)
            enableLights(true)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            setSound(
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build(),
            )
        }
        nm.createNotificationChannel(channel)
    }

    fun showBreak(ctx: Context) = show(ctx, "¡Hora de tu pausa activa!", "Tómate 5 minutos para estirarte. Toca para empezar.")

    fun showTest(ctx: Context) = show(ctx, "Prueba de Pausa Activa", "Así te avisaré cuando sea hora de moverte.")

    private fun show(ctx: Context, title: String, body: String) {
        ensureChannel(ctx)
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val openIntent = Intent(ctx, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            .putExtra(EXTRA_ACTION, ACTION_START)
        val open = PendingIntent.getActivity(ctx, 10, openIntent, flags)
        val snooze = PendingIntent.getBroadcast(
            ctx, 11, Intent(ctx, ActionReceiver::class.java).setAction(ACTION_SNOOZE), flags,
        )
        val notification = NotificationCompat.Builder(ctx, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_pausa)
            .setColor(0xFF2B6E4F.toInt())
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setShowWhen(true)
            .setWhen(System.currentTimeMillis())
            .setContentIntent(open)
            .addAction(0, "Empezar", open)
            .addAction(0, "Posponer 5 min", snooze)
            .build()
        try {
            ctx.getSystemService(NotificationManager::class.java)?.notify(NOTIF_ID, notification)
        } catch (e: SecurityException) {
            // Sin permiso de notificaciones: la interfaz ya avisa al usuario.
        }
    }

    fun cancel(ctx: Context) {
        ctx.getSystemService(NotificationManager::class.java)?.cancel(NOTIF_ID)
    }
}
