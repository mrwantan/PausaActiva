package com.pausaactiva.app.data

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/** Cálculo de horario laboral. Los días van de 0 (domingo) a 6 (sábado), igual que en la versión web. */
object Schedule {
    private val es = Locale("es", "PE")

    fun hm(s: String): Int {
        val p = s.split(":")
        return (p.getOrNull(0)?.toIntOrNull() ?: 0) * 60 + (p.getOrNull(1)?.toIntOrNull() ?: 0)
    }

    private fun atMin(base: Calendar, min: Int): Long {
        val c = base.clone() as Calendar
        c.set(Calendar.HOUR_OF_DAY, min / 60)
        c.set(Calendar.MINUTE, min % 60)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)
        return c.timeInMillis
    }

    private fun dow(c: Calendar) = c.get(Calendar.DAY_OF_WEEK) - 1

    /** Primer instante >= ts que cae dentro del horario laboral. */
    fun nextValid(ts: Long, s: Settings): Long {
        for (i in 0 until 8) {
            val d = Calendar.getInstance()
            d.timeInMillis = ts
            d.add(Calendar.DAY_OF_YEAR, i)
            if (dow(d) !in s.days) continue
            val a = atMin(d, hm(s.start))
            val b = atMin(d, hm(s.end))
            if (ts <= b) return maxOf(ts, a)
        }
        return ts
    }

    fun inWindow(ts: Long, s: Settings): Boolean {
        val d = Calendar.getInstance()
        d.timeInMillis = ts
        return dow(d) in s.days && ts >= atMin(d, hm(s.start)) && ts <= atMin(d, hm(s.end))
    }

    fun dayKey(c: Calendar = Calendar.getInstance()): String =
        String.format(Locale.US, "%04d-%02d-%02d", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH))

    /** "a las 10:00", "mañana a las 09:00" o "lunes a las 09:00". */
    fun clockText(ts: Long, now: Long): String {
        val t = SimpleDateFormat("HH:mm", es).format(Date(ts))
        val d = Calendar.getInstance().apply { timeInMillis = ts }
        val today = Calendar.getInstance().apply { timeInMillis = now }
        if (dayKey(d) == dayKey(today)) return "a las $t"
        today.add(Calendar.DAY_OF_YEAR, 1)
        if (dayKey(d) == dayKey(today)) return "mañana a las $t"
        return "${SimpleDateFormat("EEEE", es).format(Date(ts))} a las $t"
    }

    fun mmss(ms: Long): String {
        val s = maxOf(0L, (ms + 999) / 1000)
        val h = s / 3600
        val m = (s % 3600) / 60
        val x = s % 60
        return if (h > 0) String.format(Locale.US, "%d:%02d:%02d", h, m, x) else String.format(Locale.US, "%02d:%02d", m, x)
    }
}
