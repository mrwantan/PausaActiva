@file:OptIn(ExperimentalMaterial3Api::class)

package com.pausaactiva.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pausaactiva.app.data.Exercises
import com.pausaactiva.app.data.Schedule

@Composable
fun SessionScreen(s: SessionState, vm: AppViewModel) {
    val p = LocalPal.current
    val view = LocalView.current
    DisposableEffect(view) {
        view.keepScreenOn = true
        onDispose { view.keepScreenOn = false }
    }
    BackHandler { vm.closeSession() }

    val seg = s.segs[s.idx]
    val leftMs = seg.dur * 1000L - s.elMs
    val totalLeft = s.segs.withIndex().sumOf { (i, sg) -> if (i > s.idx) sg.dur * 1000L else 0L } + leftMs
    val tone = p.tones[Exercises.indexOf(seg.ex.id) % p.tones.size]

    Box(Modifier.fillMaxSize().background(p.bg).systemBarsPadding()) {
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { vm.closeSession() }) {
                    Icon(Icons.Filled.Close, contentDescription = "Cerrar", tint = p.ink)
                }
                Column {
                    Text("Ejercicio ${seg.i + 1} de ${s.routine.size}", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = p.ink)
                    Text(
                        if (s.finished) "Completada" else "${Schedule.mmss(totalLeft)} restantes",
                        fontSize = 13.sp, color = p.text2,
                    )
                }
            }

            Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                s.routine.indices.forEach { i ->
                    val pd = s.segs[i * 2].dur
                    val unit = (pd + STEP_SEC).toFloat()
                    val done = when {
                        i < seg.i -> 1f
                        i > seg.i -> 0f
                        !seg.prep -> (pd + s.elMs / 1000f) / unit
                        else -> (s.elMs / 1000f) / unit
                    }
                    Bar(done, p.sGreen, p.line, 5.dp, Modifier.weight(1f))
                }
            }

            Column(
                Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    Modifier.widthIn(max = 280.dp).fillMaxWidth(0.75f).aspectRatio(1f)
                        .clip(RoundedCornerShape(14.dp)).background(tone).padding(16.dp),
                ) {
                    FigureView(seg.ex.id, tone, running = s.running, modifier = Modifier.fillMaxSize())
                }
                Tag(if (seg.prep) "Prepárate" else "${seg.ex.posture} · ${seg.ex.zone}")
                Text(
                    seg.ex.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = p.ink,
                    textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp),
                )
                Text(
                    if (seg.prep) {
                        if (seg.changed) {
                            if (seg.ex.posture == "De pie") "Ponte de pie junto a tu escritorio" else "Siéntate con la espalda recta"
                        } else "Prepárate para el siguiente"
                    } else seg.ex.text,
                    fontSize = 15.sp, color = p.text2, textAlign = TextAlign.Center, lineHeight = 22.sp,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }

            Column(Modifier.padding(horizontal = 20.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "${maxOf(0, ((leftMs + 999) / 1000).toInt())}",
                    fontSize = 52.sp, fontWeight = FontWeight.Bold, color = p.ink,
                    textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(),
                )
                Bar(
                    (s.elMs / (seg.dur * 1000f)),
                    if (seg.prep) p.sBlue else p.accent, p.line, 6.dp, Modifier.fillMaxWidth(),
                )
                Row(
                    Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedIconButton(onClick = { vm.jump(-1) }, modifier = Modifier.size(56.dp)) {
                        Icon(Icons.Filled.SkipPrevious, contentDescription = "Anterior", tint = p.ink)
                    }
                    FilledIconButton(
                        onClick = { vm.togglePlay() },
                        modifier = Modifier.padding(horizontal = 20.dp).size(76.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = p.accent, contentColor = p.onAccent),
                    ) {
                        Icon(
                            if (s.running) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (s.running) "Pausar" else "Reanudar",
                            modifier = Modifier.size(32.dp),
                        )
                    }
                    OutlinedIconButton(onClick = { vm.jump(1) }, modifier = Modifier.size(56.dp)) {
                        Icon(Icons.Filled.SkipNext, contentDescription = "Siguiente", tint = p.ink)
                    }
                }
            }
        }

        if (s.finished) {
            Column(
                Modifier.fillMaxSize().background(p.bg).padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
            ) {
                Box(
                    Modifier.size(84.dp).clip(RoundedCornerShape(20.dp)).background(p.green),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Check, contentDescription = null, tint = p.sGreen, modifier = Modifier.size(44.dp))
                }
                Text("Pausa completada", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = p.ink)
                Text(s.doneText, fontSize = 15.sp, color = p.text2, textAlign = TextAlign.Center)
                Button(
                    onClick = { vm.closeSession() },
                    colors = ButtonDefaults.buttonColors(containerColor = p.accent, contentColor = p.onAccent),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                ) { Text("Volver al trabajo", fontWeight = FontWeight.SemiBold) }
            }
        }
    }
}
