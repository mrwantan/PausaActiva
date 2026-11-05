@file:OptIn(ExperimentalMaterial3Api::class)

package com.pausaactiva.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pausaactiva.app.data.Schedule
import java.util.Calendar

private val TIPS = listOf(
    "Todos los ejercicios se hacen sentado o de pie junto a tu escritorio. No hace falta echarte al piso.",
    "Regla 20-20-20: cada 20 minutos mira algo a 6 metros durante 20 segundos.",
    "Toma un vaso de agua después de cada pausa: te hidrata y te obliga a levantarte.",
    "Apoya los pies completos en el suelo y la espalda en el respaldo cuando estés sentado.",
    "Si sientes dolor (no tensión suave), detén el ejercicio y avanza al siguiente.",
    "Los avisos funcionan con la app cerrada: no necesitas dejarla abierta.",
)

@Composable
fun HomeScreen(ui: AppViewModel.Ui, vm: AppViewModel, act: SystemActions) {
    val p = LocalPal.current
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Pausa Activa", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = p.ink)

        if (ui.enabled && ui.due) {
            Callout(p.orange) {
                Text("Es hora de tu pausa", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = p.ink)
                Text("Son 5 minutos para estirarte.", fontSize = 14.sp, color = p.text2)
                Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { vm.startSession() },
                        colors = ButtonDefaults.buttonColors(containerColor = p.accent, contentColor = p.onAccent),
                    ) { Text("Empezar") }
                    OutlinedButton(onClick = { vm.snooze() }) { Text("Posponer 5 min", color = p.ink) }
                }
            }
        }

        Ring(ui)

        Button(
            onClick = { if (ui.enabled) vm.disable() else vm.enable() },
            modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
            shape = RoundedCornerShape(8.dp),
            colors = if (ui.enabled) {
                ButtonDefaults.buttonColors(containerColor = p.surface, contentColor = p.ink)
            } else {
                ButtonDefaults.buttonColors(containerColor = p.accent, contentColor = p.onAccent)
            },
        ) {
            Icon(if (ui.enabled) Icons.Filled.NotificationsOff else Icons.Filled.Notifications, contentDescription = null)
            Text(
                if (ui.enabled) "  Pausar recordatorios" else "  Activar recordatorios",
                fontWeight = FontWeight.SemiBold, fontSize = 15.sp,
            )
        }
        FilledTonalButton(
            onClick = { vm.startSession() },
            modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.filledTonalButtonColors(containerColor = p.blue, contentColor = p.ink),
        ) {
            Icon(Icons.Filled.PlayArrow, contentDescription = null)
            Text("  Hacer una pausa ahora", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        }

        if (!ui.allOk) ChecklistCard(ui, act)

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Stat(p.orange, ui.today.toString(), "pausas hoy", Modifier.weight(1f))
            Stat(p.purple, ui.streak.toString(), "días seguidos", Modifier.weight(1f))
        }

        Callout(p.pink) {
            Text("Tip de oficina", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = p.ink)
            Text(
                TIPS[Calendar.getInstance().get(Calendar.DAY_OF_MONTH) % TIPS.size],
                fontSize = 14.sp, color = p.text2, lineHeight = 20.sp,
            )
        }
    }
}

@Composable
private fun Stat(color: androidx.compose.ui.graphics.Color, big: String, cap: String, modifier: Modifier) {
    val p = LocalPal.current
    Callout(color, modifier) {
        Text(big, fontSize = 34.sp, fontWeight = FontWeight.Bold, color = p.ink)
        Text(cap, fontSize = 13.sp, color = p.text2)
    }
}

@Composable
private fun Ring(ui: AppViewModel.Ui) {
    val p = LocalPal.current
    val label: String
    val time: String
    val sub: String
    val frac: Float
    when {
        !ui.enabled -> {
            label = "Recordatorios"; time = "En pausa"
            sub = "Cada ${ui.s.interval} min\n${ui.s.start} – ${ui.s.end}"; frac = 0f
        }
        ui.due -> {
            label = "¡Ahora!"; time = "Pausa"
            sub = "Siguiente ${Schedule.clockText(ui.nextDue, ui.now)}"; frac = 1f
        }
        else -> {
            val rem = ui.nextDue - ui.now
            val tot = ui.s.interval * 60_000f
            label = "Próxima pausa en"; time = Schedule.mmss(rem)
            sub = Schedule.clockText(ui.nextDue, ui.now); frac = (1f - rem / tot).coerceIn(0f, 1f)
        }
    }
    val small = !ui.enabled || ui.due
    Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = p.green) {
        Box(Modifier.padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
            Box(Modifier.size(240.dp), contentAlignment = Alignment.Center) {
                val track = p.line
                val prog = if (ui.enabled) p.sGreen else p.text3
                Canvas(Modifier.fillMaxSize()) {
                    val w = 12.dp.toPx()
                    val sz = Size(size.width - w, size.height - w)
                    val tl = Offset(w / 2, w / 2)
                    drawArc(track, 0f, 360f, false, topLeft = tl, size = sz, style = Stroke(w))
                    drawArc(prog, -90f, 360f * frac, false, topLeft = tl, size = sz, style = Stroke(w, cap = StrokeCap.Round))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 32.dp)) {
                    Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = p.text2)
                    Text(
                        time, fontSize = if (small) 28.sp else 46.sp, fontWeight = FontWeight.Bold, color = p.ink,
                        maxLines = 1,
                    )
                    Text(sub, fontSize = 14.sp, color = p.text2, textAlign = TextAlign.Center)
                }
            }
        }
    }
}
