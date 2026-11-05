package com.pausaactiva.app.data

import android.content.Context
import org.json.JSONObject
import java.util.Calendar

data class Settings(
    val interval: Int = 60,
    val start: String = "09:00",
    val end: String = "18:00",
    val days: Set<Int> = setOf(1, 2, 3, 4, 5),
    val sound: Boolean = true,
    val vibrate: Boolean = true,
)

/** Estado persistente. Lo leen la interfaz y los receptores (alarma, arranque), así que todo vive en SharedPreferences. */
class Prefs private constructor(ctx: Context) {
    private val sp = ctx.applicationContext.getSharedPreferences("pausa_activa", Context.MODE_PRIVATE)

    companion object {
        fun get(ctx: Context) = Prefs(ctx)
    }

    var interval: Int
        get() = sp.getInt("interval", 60)
        set(v) { sp.edit().putInt("interval", v).apply() }
    var start: String
        get() = sp.getString("start", "09:00") ?: "09:00"
        set(v) { sp.edit().putString("start", v).apply() }
    var end: String
        get() = sp.getString("end", "18:00") ?: "18:00"
        set(v) { sp.edit().putString("end", v).apply() }
    var days: Set<Int>
        get() = (sp.getString("days", "1,2,3,4,5") ?: "").split(",").mapNotNull { it.trim().toIntOrNull() }.toSet()
        set(v) { sp.edit().putString("days", v.sorted().joinToString(",")).apply() }
    var sound: Boolean
        get() = sp.getBoolean("sound", true)
        set(v) { sp.edit().putBoolean("sound", v).apply() }
    var vibrate: Boolean
        get() = sp.getBoolean("vibrate", true)
        set(v) { sp.edit().putBoolean("vibrate", v).apply() }

    var enabled: Boolean
        get() = sp.getBoolean("enabled", false)
        set(v) { sp.edit().putBoolean("enabled", v).apply() }
    var nextDue: Long
        get() = sp.getLong("nextDue", 0L)
        set(v) { sp.edit().putLong("nextDue", v).apply() }
    var snoozeActive: Boolean
        get() = sp.getBoolean("snoozeActive", false)
        set(v) { sp.edit().putBoolean("snoozeActive", v).apply() }
    /** Última vez que sonó un aviso. */
    var lastFire: Long
        get() = sp.getLong("lastFire", 0L)
        set(v) { sp.edit().putLong("lastFire", v).apply() }
    /** Última vez que el usuario respondió al aviso (empezó la pausa o la pospuso). */
    var lastAck: Long
        get() = sp.getLong("lastAck", 0L)
        set(v) { sp.edit().putLong("lastAck", v).apply() }
    var onboarded: Boolean
        get() = sp.getBoolean("onboarded", false)
        set(v) { sp.edit().putBoolean("onboarded", v).apply() }
    var notifAsked: Boolean
        get() = sp.getBoolean("notifAsked", false)
        set(v) { sp.edit().putBoolean("notifAsked", v).apply() }
    /** Número de rutinas hechas (para rotar ejercicios). */
    var n: Int
        get() = sp.getInt("n", 0)
        set(v) { sp.edit().putInt("n", v).apply() }

    fun settings() = Settings(interval, start, end, days, sound, vibrate)

    // ---- Historial de pausas por día ----
    private fun json(key: String): JSONObject =
        try { JSONObject(sp.getString(key, "{}") ?: "{}") } catch (e: Exception) { JSONObject() }

    fun historyCount(dayKey: String): Int = json("history").optInt(dayKey, 0)

    fun todayCount(): Int = historyCount(Schedule.dayKey())

    fun addDone(dayKey: String) {
        val h = json("history")
        h.put(dayKey, h.optInt(dayKey, 0) + 1)
        sp.edit().putString("history", h.toString()).apply()
    }

    fun streak(): Int {
        var count = 0
        val c = Calendar.getInstance()
        if (todayCount() == 0) c.add(Calendar.DAY_OF_YEAR, -1)
        while (historyCount(Schedule.dayKey(c)) > 0) {
            count++
            c.add(Calendar.DAY_OF_YEAR, -1)
        }
        return count
    }

    // ---- Uso de ejercicios (para que la rutina rote) ----
    fun useOf(id: String): Int = json("use").optInt(id, 0)

    fun markUsed(ids: List<String>, round: Int) {
        val u = json("use")
        ids.forEach { u.put(it, round) }
        sp.edit().putString("use", u.toString()).apply()
    }
}
