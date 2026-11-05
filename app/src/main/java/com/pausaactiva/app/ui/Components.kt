@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.pausaactiva.app.ui

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.view.View
import android.webkit.WebView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.pausaactiva.app.data.Schedule

/** Tarjeta con borde fino, como los bloques de Notion. */
@Composable
fun BlockCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val p = LocalPal.current
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = p.surface,
        border = BorderStroke(1.dp, p.line),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) { content() }
    }
}

@Composable
fun SectionLabel(text: String) {
    val p = LocalPal.current
    Text(text.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = p.text2, letterSpacing = 0.6.sp)
}

/** Recuadro de color pastel con icono opcional. */
@Composable
fun Callout(color: Color, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Surface(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), color = color) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            content()
        }
    }
}

@Composable
fun PChip(text: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val p = LocalPal.current
    Surface(
        onClick = onClick,
        modifier = modifier.heightIn(min = 40.dp),
        shape = RoundedCornerShape(8.dp),
        color = if (selected) p.blue else p.surface,
        border = BorderStroke(1.dp, if (selected) p.sBlue else p.line),
    ) {
        Box(Modifier.padding(horizontal = 14.dp), contentAlignment = Alignment.Center) {
            Text(
                text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                color = if (selected) p.ink else p.text2,
            )
        }
    }
}

@Composable
fun Bar(frac: Float, color: Color, track: Color, height: Dp, modifier: Modifier = Modifier) {
    Box(modifier.height(height).clip(RoundedCornerShape(height)).background(track)) {
        Box(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(frac.coerceIn(0f, 1f))
                .background(color),
        )
    }
}

/**
 * Figura animada del ejercicio. Reutiliza las animaciones SVG de la versión web (assets/exercises.js + figures.css)
 * dentro de un WebView local: funciona sin internet.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun FigureView(id: String, tone: Color, running: Boolean = true, modifier: Modifier = Modifier) {
    val p = LocalPal.current
    val url = remember(id, p, tone) {
        "file:///android_asset/figure.html#id=$id&ink=${p.ink.hex6()}&surface=${p.surface.hex6()}&tone=${tone.hex6()}"
    }
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            WebView(ctx).apply {
                setBackgroundColor(0)
                settings.javaScriptEnabled = true
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false
                overScrollMode = View.OVER_SCROLL_NEVER
                tag = url
                loadUrl(url)
            }
        },
        update = { wv ->
            if (wv.tag != url) {
                wv.tag = url
                wv.loadUrl(url)
            }
            wv.evaluateJavascript("window.setRun && window.setRun($running)", null)
        },
        onRelease = { it.destroy() },
    )
}

private val DAY_NAMES = listOf("D", "L", "M", "X", "J", "V", "S")
private val DAY_FULL = listOf("Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado")

private fun intervalLabel(m: Int) = when (m) {
    60 -> "1 hora"
    90 -> "1 h 30"
    120 -> "2 horas"
    else -> "$m min"
}

@Composable
private fun TimeButton(label: String, value: String, modifier: Modifier, onPick: (String) -> Unit) {
    val ctx = LocalContext.current
    val p = LocalPal.current
    OutlinedButton(
        onClick = {
            val mins = Schedule.hm(value)
            TimePickerDialog(ctx, { _, h, m -> onPick(String.format("%02d:%02d", h, m)) }, mins / 60, mins % 60, true).show()
        },
        modifier = modifier.heightIn(min = 52.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, p.line),
    ) {
        Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
            Text(label, fontSize = 12.sp, color = p.text2)
            Text(value, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = p.ink)
        }
    }
}

/** Frecuencia, horario y días: se usa en Ajustes y en la introducción. */
@Composable
fun ScheduleControls(ui: AppViewModel.Ui, vm: AppViewModel, act: SystemActions) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionLabel("Frecuencia")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(30, 45, 60, 90, 120).forEach { m ->
                PChip(intervalLabel(m), ui.s.interval == m) { vm.setInterval(m) }
            }
        }
        SectionLabel("Horario laboral")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TimeButton("Desde", ui.s.start, Modifier.weight(1f)) { vm.setStart(it) }
            TimeButton("Hasta", ui.s.end, Modifier.weight(1f)) { vm.setEnd(it) }
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(1, 2, 3, 4, 5, 6, 0).forEach { d ->
                PChip(DAY_NAMES[d], d in ui.s.days) {
                    if (!vm.toggleDay(d)) act.toast("Deja al menos un día activo.")
                }
            }
        }
    }
}

@Composable
private fun CheckRow(ok: Boolean, title: String, detail: String, action: String, onFix: () -> Unit) {
    val p = LocalPal.current
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Icon(
            if (ok) Icons.Filled.CheckCircle else Icons.Filled.Warning,
            contentDescription = null,
            tint = if (ok) p.sGreen else p.sOrange,
            modifier = Modifier.size(24.dp),
        )
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = p.ink)
            Text(detail, fontSize = 13.sp, color = p.text2, lineHeight = 18.sp)
        }
        if (!ok) TextButton(onClick = onFix) { Text(action, color = p.sBlue, fontWeight = FontWeight.SemiBold) }
    }
}

/** Lo que Android exige para que el aviso llegue con la app cerrada. */
@Composable
fun ChecklistCard(ui: AppViewModel.Ui, act: SystemActions) {
    BlockCard {
        SectionLabel("Para que el aviso no falle")
        CheckRow(
            ui.notifOk, "Notificaciones",
            if (ui.notifOk) "Permitidas" else "Bloqueadas o sin importancia alta: no sonará ni aparecerá.",
            "Arreglar", act::fixNotifications,
        )
        CheckRow(
            ui.exactOk, "Alarmas exactas",
            if (ui.exactOk) "El aviso llega a la hora exacta" else "Sin esto Android puede retrasar el aviso varios minutos.",
            "Permitir", act::openExactAlarmSettings,
        )
        CheckRow(
            ui.batteryOk, "Batería sin restricciones",
            if (ui.batteryOk) "Android no dormirá la app" else "Evita que Android congele la app en segundo plano.",
            "Permitir", act::requestBatteryExemption,
        )
    }
}
