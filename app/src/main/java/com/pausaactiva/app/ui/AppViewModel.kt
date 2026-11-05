package com.pausaactiva.app.ui

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.PowerManager
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pausaactiva.app.data.Exercise
import com.pausaactiva.app.data.Exercises
import com.pausaactiva.app.data.Prefs
import com.pausaactiva.app.data.Schedule
import com.pausaactiva.app.data.Settings
import com.pausaactiva.app.notify.AlarmScheduler
import com.pausaactiva.app.notify.Notifier
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

const val STEP_SEC = 38      // duración de cada ejercicio
const val PREP_SEC = 3       // transición entre ejercicios de la misma postura
const val CHANGE_PREP = 8    // más tiempo al cambiar de sentado a de pie (y al empezar)

data class Seg(val prep: Boolean, val ex: Exercise, val i: Int, val dur: Int, val changed: Boolean)

data class SessionState(
    val routine: List<Exercise>,
    val segs: List<Seg>,
    val idx: Int = 0,
    val elMs: Long = 0L,
    val running: Boolean = true,
    val finished: Boolean = false,
    val doneText: String = "",
)

class AppViewModel(app: Application) : AndroidViewModel(app) {
    private val ctx: Context = app.applicationContext
    private val prefs = Prefs.get(ctx)

    data class Ui(
        val now: Long,
        val s: Settings,
        val enabled: Boolean,
        val nextDue: Long,
        val due: Boolean,
        val today: Int,
        val streak: Int,
        val notifOk: Boolean,
        val exactOk: Boolean,
        val batteryOk: Boolean,
        val onboarded: Boolean,
    ) {
        val allOk: Boolean get() = notifOk && exactOk && batteryOk
    }

    private fun read(): Ui {
        val nm = ctx.getSystemService(NotificationManager::class.java)
        val channel = nm?.getNotificationChannel(Notifier.CHANNEL_ID)
        val notifOk = NotificationManagerCompat.from(ctx).areNotificationsEnabled() &&
            (channel == null || channel.importance >= NotificationManager.IMPORTANCE_DEFAULT)
        val pm = ctx.getSystemService(Context.POWER_SERVICE) as PowerManager
        return Ui(
            now = System.currentTimeMillis(),
            s = prefs.settings(),
            enabled = prefs.enabled,
            nextDue = prefs.nextDue,
            due = prefs.enabled && prefs.lastFire > prefs.lastAck,
            today = prefs.todayCount(),
            streak = prefs.streak(),
            notifOk = notifOk,
            exactOk = AlarmScheduler.canExact(ctx),
            batteryOk = pm.isIgnoringBatteryOptimizations(ctx.packageName),
            onboarded = prefs.onboarded,
        )
    }

    private val _ui = MutableStateFlow(read())
    val ui: StateFlow<Ui> = _ui

    private val _session = MutableStateFlow<SessionState?>(null)
    val session: StateFlow<SessionState?> = _session

    private var sessionJob: Job? = null
    private var tone: ToneGenerator? = null
    private var lastCountdown = -1

    init {
        viewModelScope.launch {
            while (true) {
                _ui.value = read()
                delay(1000)
            }
        }
    }

    fun refresh() {
        _ui.value = read()
    }

    // ---------- Recordatorios ----------
    fun enable() {
        AlarmScheduler.enable(ctx)
        refresh()
    }

    fun disable() {
        AlarmScheduler.disable(ctx)
        refresh()
    }

    fun snooze() {
        AlarmScheduler.snooze(ctx)
        refresh()
    }

    fun completeOnboarding() {
        prefs.onboarded = true
        refresh()
    }

    fun resetOnboarding() {
        prefs.onboarded = false
        refresh()
    }

    fun testNow() = Notifier.showTest(ctx)

    fun testIn10s() = AlarmScheduler.testIn(ctx, 10_000L)

    // ---------- Ajustes ----------
    private fun afterSettings() {
        AlarmScheduler.reschedule(ctx)
        refresh()
    }

    fun setInterval(m: Int) { prefs.interval = m; afterSettings() }
    fun setStart(hhmm: String) { prefs.start = hhmm; afterSettings() }
    fun setEnd(hhmm: String) { prefs.end = hhmm; afterSettings() }

    /** Devuelve false si se intentó quitar el último día. */
    fun toggleDay(d: Int): Boolean {
        val cur = prefs.days
        val next = if (d in cur) cur - d else cur + d
        if (next.isEmpty()) return false
        prefs.days = next
        afterSettings()
        return true
    }

    fun setSound(v: Boolean) { prefs.sound = v; refresh(); if (v) beep(150) }
    fun setVibrate(v: Boolean) { prefs.vibrate = v; refresh(); if (v) buzz(120) }

    // ---------- Sesión de pausa (5 minutos) ----------
    private fun beep(ms: Int) {
        if (!prefs.sound) return
        try {
            if (tone == null) tone = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 70)
            tone?.startTone(ToneGenerator.TONE_PROP_BEEP, ms)
        } catch (e: Exception) {
            // sin audio disponible
        }
    }

    private fun buzz(ms: Long) {
        if (!prefs.vibrate) return
        try {
            ctx.getSystemService(Vibrator::class.java)
                ?.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE))
        } catch (e: Exception) {
            // sin vibrador
        }
    }

    fun startSession() {
        if (_session.value != null) return
        prefs.lastAck = System.currentTimeMillis()
        Notifier.cancel(ctx)
        val routine = Exercises.buildRoutine(prefs)
        val segs = ArrayList<Seg>()
        routine.forEachIndexed { i, ex ->
            val changed = i == 0 || ex.posture != routine[i - 1].posture
            segs.add(Seg(true, ex, i, if (changed) CHANGE_PREP else PREP_SEC, changed))
            segs.add(Seg(false, ex, i, STEP_SEC, changed))
        }
        lastCountdown = -1
        _session.value = SessionState(routine, segs)
        sessionJob?.cancel()
        sessionJob = viewModelScope.launch {
            var last = SystemClock.elapsedRealtime()
            while (true) {
                delay(200)
                val now = SystemClock.elapsedRealtime()
                val dt = now - last
                last = now
                tickSession(dt)
            }
        }
        refresh()
    }

    private fun tickSession(dt: Long) {
        val s = _session.value ?: return
        if (!s.running || s.finished) return
        val seg = s.segs[s.idx]
        val el = s.elMs + dt
        if (el >= seg.dur * 1000L) {
            if (s.idx + 1 >= s.segs.size) {
                finishSession(s)
                return
            }
            val next = s.copy(idx = s.idx + 1, elMs = 0L)
            _session.value = next
            lastCountdown = -1
            if (next.segs[next.idx].prep) { beep(120); buzz(80) } else { beep(250); buzz(150) }
        } else {
            val left = ((seg.dur * 1000L - el + 999) / 1000).toInt()
            if (seg.prep && left <= 3 && left != lastCountdown) {
                lastCountdown = left
                beep(80)
            }
            _session.value = s.copy(elMs = el)
        }
    }

    fun togglePlay() {
        val s = _session.value ?: return
        _session.value = s.copy(running = !s.running)
    }

    fun jump(dir: Int) {
        val s = _session.value ?: return
        if (s.finished) return
        val seg = s.segs[s.idx]
        val target = if (dir > 0) seg.i + 1 else if (seg.prep && s.elMs < 3000) seg.i - 1 else seg.i
        if (target >= s.routine.size) {
            finishSession(s)
            return
        }
        lastCountdown = -1
        _session.value = s.copy(idx = maxOf(0, target) * 2, elMs = 0L)
    }

    private fun finishSession(s: SessionState) {
        sessionJob?.cancel()
        val now = System.currentTimeMillis()
        prefs.addDone(Schedule.dayKey())
        prefs.lastAck = now
        if (prefs.enabled) AlarmScheduler.reschedule(ctx)
        val n = prefs.todayCount()
        val text = "Llevas $n ${if (n == 1) "pausa" else "pausas"} hoy." +
            if (prefs.enabled) " Próximo aviso ${Schedule.clockText(prefs.nextDue, now)}." else ""
        beep(400)
        buzz(300)
        _session.value = s.copy(finished = true, running = false, doneText = text)
        refresh()
    }

    fun closeSession() {
        sessionJob?.cancel()
        _session.value = null
        refresh()
    }

    override fun onCleared() {
        sessionJob?.cancel()
        tone?.release()
        super.onCleared()
    }
}
