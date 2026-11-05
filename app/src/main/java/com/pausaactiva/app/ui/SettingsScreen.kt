@file:OptIn(ExperimentalMaterial3Api::class)

package com.pausaactiva.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(ui: AppViewModel.Ui, vm: AppViewModel, act: SystemActions) {
    val p = LocalPal.current
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Ajustes", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = p.ink)

        ChecklistCard(ui, act)

        BlockCard {
            SectionLabel("Pruebas")
            OutlinedButton(
                onClick = {
                    vm.testNow()
                    act.toast("Notificación enviada. Si no la ves, revisa la lista de arriba.")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
            ) { Text("Probar notificación ahora", color = p.ink) }
            OutlinedButton(
                onClick = {
                    vm.testIn10s()
                    act.toast("Cierra la app y espera 10 segundos: debe sonar el aviso.")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
            ) { Text("Probar con la app cerrada (10 s)", color = p.ink) }
            Text(
                "La segunda prueba programa una alarma real del sistema. Si llega con la app cerrada, los recordatorios funcionarán.",
                fontSize = 13.sp, color = p.text2, lineHeight = 18.sp,
            )
            TextButton(onClick = act::openNotificationSettings) {
                Text("Sonido e importancia del aviso (Android)", color = p.sBlue, fontWeight = FontWeight.SemiBold)
            }
        }

        BlockCard { ScheduleControls(ui, vm, act) }

        BlockCard {
            SectionLabel("Durante la pausa")
            SwitchRow("Sonido", "Pitidos al cambiar de ejercicio", ui.s.sound) { vm.setSound(it) }
            SwitchRow("Vibración", "Al cambiar de ejercicio", ui.s.vibrate) { vm.setVibrate(it) }
        }

        BlockCard {
            SectionLabel("Ayuda")
            OutlinedButton(
                onClick = { vm.resetOnboarding() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
            ) { Text("Ver la introducción", color = p.ink) }
            Text(
                "¿Xiaomi, Huawei, Oppo, Samsung u otra marca que cierra apps? Algunas necesitan un permiso extra de «inicio automático».",
                fontSize = 13.sp, color = p.text2, lineHeight = 18.sp,
            )
            TextButton(onClick = { act.openUrl("https://dontkillmyapp.com/") }) {
                Text("Ver guía por marca (dontkillmyapp.com)", color = p.sBlue, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun SwitchRow(title: String, detail: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    val p = LocalPal.current
    Row(
        Modifier.fillMaxWidth().clickable { onChange(!checked) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = p.ink)
            Text(detail, fontSize = 13.sp, color = p.text2)
        }
        Switch(checked = checked, onCheckedChange = onChange)
    }
}
