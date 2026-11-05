package com.pausaactiva.app.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationManagerCompat
import com.pausaactiva.app.data.Prefs
import com.pausaactiva.app.notify.Notifier

/** Puentes hacia pantallas del sistema (permisos, batería, ajustes del canal). Se crea una vez en la Activity. */
class SystemActions(private val activity: ComponentActivity) {
    private val ctx: Context get() = activity
    private val notifLauncher = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    private fun launch(intent: Intent) {
        try {
            activity.startActivity(intent)
        } catch (e: Exception) {
            toast("No pude abrir esa pantalla de Ajustes.")
        }
    }

    fun toast(msg: String) = Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show()

    /** Primera vez: diálogo del sistema (Android 13+). Si ya se negó, lleva a los ajustes de notificaciones de la app. */
    fun fixNotifications() {
        val prefs = Prefs.get(ctx)
        val granted = Build.VERSION.SDK_INT < 33 ||
            ctx.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        if (!granted && !prefs.notifAsked) {
            prefs.notifAsked = true
            notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            openNotificationSettings()
        }
    }

    fun openNotificationSettings() {
        val appEnabled = NotificationManagerCompat.from(ctx).areNotificationsEnabled()
        val intent = if (appEnabled) {
            Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, ctx.packageName)
                .putExtra(Settings.EXTRA_CHANNEL_ID, Notifier.CHANNEL_ID)
        } else {
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, ctx.packageName)
        }
        launch(intent)
    }

    fun openExactAlarmSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            launch(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:${ctx.packageName}")))
        }
    }

    fun requestBatteryExemption() {
        try {
            activity.startActivity(
                Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:${ctx.packageName}")),
            )
        } catch (e: Exception) {
            launch(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
        }
    }

    fun openUrl(url: String) = launch(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}
