@file:OptIn(ExperimentalMaterial3Api::class)

package com.pausaactiva.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessAlarm
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val PAGES = 4

@Composable
fun OnboardingScreen(ui: AppViewModel.Ui, vm: AppViewModel, act: SystemActions) {
    val p = LocalPal.current
    var page by rememberSaveable { mutableIntStateOf(0) }

    Column(Modifier.fillMaxSize().background(p.bg).systemBarsPadding()) {
        Row(
            Modifier.fillMaxWidth().padding(start = 24.dp, end = 8.dp, top = 8.dp).heightIn(min = 48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                repeat(PAGES) { i ->
                    Box(
                        Modifier.size(width = if (i == page) 22.dp else 8.dp, height = 8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (i == page) p.ink else p.line),
                    )
                }
            }
            if (page < PAGES - 1) {
                TextButton(onClick = { vm.completeOnboarding() }) { Text("Omitir", color = p.text2) }
            }
        }

        Column(
            Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            when (page) {
                0 -> {
                    FigureCard("reach", 0)
                    Heading("Muévete cada hora")
                    Body("Te aviso para hacer una pausa activa de 5 minutos y cuidar tu espalda, cuello y ojos durante la jornada.")
                }
                1 -> {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.widthIn(max = 340.dp)) {
                        FigureCard("neck-tilt", 1, Modifier.weight(1f))
                        FigureCard("squat", 2, Modifier.weight(1f))
                    }
                    Heading("Ejercicios para la oficina")
                    Body("Todos se hacen sentado o de pie junto a tu escritorio. Sin echarte al piso ni hacer cosas raras, y la rutina cambia cada vez.")
                }
                2 -> {
                    Heading("Tu horario")
                    Body("Elige cada cuánto quieres el aviso y en qué horario trabajas. Lo puedes cambiar después en Ajustes.")
                    Column(Modifier.widthIn(max = 380.dp).fillMaxWidth()) { ScheduleControls(ui, vm, act) }
                }
                else -> {
                    Box(
                        Modifier.size(72.dp).clip(RoundedCornerShape(16.dp)).background(p.yellow),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Filled.AccessAlarm, contentDescription = null, tint = p.sOrange, modifier = Modifier.size(36.dp))
                    }
                    Heading("Activa el aviso")
                    Body("Para que la alarma llegue a tiempo aunque cierres la app, concede estos tres permisos. Todo funciona sin internet.")
                    Column(Modifier.widthIn(max = 420.dp).fillMaxWidth()) { ChecklistCard(ui, act) }
                }
            }
        }

        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (page > 0) {
                OutlinedButton(
                    onClick = { page -= 1 },
                    modifier = Modifier.weight(1f).heightIn(min = 52.dp),
                    shape = RoundedCornerShape(8.dp),
                ) { Text("Atrás", color = p.ink) }
            }
            Button(
                onClick = {
                    if (page < PAGES - 1) page += 1
                    else {
                        vm.completeOnboarding()
                        vm.enable()
                    }
                },
                modifier = Modifier.weight(1f).heightIn(min = 52.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = p.accent, contentColor = p.onAccent),
            ) { Text(if (page < PAGES - 1) "Siguiente" else "Activar y empezar", fontWeight = FontWeight.SemiBold) }
        }
    }
}

@Composable
private fun FigureCard(id: String, toneIndex: Int, modifier: Modifier = Modifier) {
    val p = LocalPal.current
    val tone = p.tones[toneIndex % p.tones.size]
    Box(
        modifier.widthIn(max = 240.dp).aspectRatio(1f).clip(RoundedCornerShape(16.dp)).background(tone).padding(16.dp),
    ) {
        FigureView(id, tone, modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun Heading(text: String) {
    Text(
        text, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = LocalPal.current.ink,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun Body(text: String) {
    Text(
        text, fontSize = 15.sp, color = LocalPal.current.text2, textAlign = TextAlign.Center,
        lineHeight = 22.sp, modifier = Modifier.widthIn(max = 420.dp),
    )
}
