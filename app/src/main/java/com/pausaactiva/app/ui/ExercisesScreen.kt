@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.pausaactiva.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pausaactiva.app.data.Exercise
import com.pausaactiva.app.data.Exercises

@Composable
fun ExercisesScreen() {
    val p = LocalPal.current
    var filter by remember { mutableStateOf("Todos") }
    var selected by remember { mutableStateOf<Exercise?>(null) }
    val list = Exercises.all.filter { filter == "Todos" || it.posture == filter }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Ejercicios", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = p.ink)
                Text(
                    "Se hacen sentado o de pie junto a tu escritorio. Sin tirarte al piso.",
                    fontSize = 14.sp, color = p.text2, lineHeight = 20.sp,
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Todos", "Sentado", "De pie").forEach { f -> PChip(f, filter == f) { filter = f } }
                }
            }
        }
        items(list, key = { it.id }) { ex ->
            val tone = p.tones[Exercises.indexOf(ex.id) % p.tones.size]
            Surface(
                modifier = Modifier.fillMaxWidth().clickable { selected = ex },
                shape = RoundedCornerShape(10.dp),
                color = p.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, p.line),
            ) {
                Column {
                    Box(
                        Modifier.fillMaxWidth().aspectRatio(1.6f).background(tone),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Filled.SelfImprovement, contentDescription = null, tint = p.sGreen, modifier = Modifier.size(44.dp))
                    }
                    Column(Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                        Text(ex.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = p.ink, lineHeight = 19.sp)
                        Text("${ex.posture} · ${ex.zone}", fontSize = 12.sp, color = p.text2, lineHeight = 16.sp)
                    }
                }
            }
        }
    }

    selected?.let { ex ->
        ModalBottomSheet(onDismissRequest = { selected = null }, containerColor = p.surface) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                val tone = p.tones[Exercises.indexOf(ex.id) % p.tones.size]
                Box(
                    Modifier.widthIn(max = 240.dp).fillMaxWidth(0.7f).aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp)).background(tone).padding(12.dp),
                ) {
                    FigureView(ex.id, tone, modifier = Modifier.fillMaxWidth().aspectRatio(1f))
                }
                Text(ex.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = p.ink)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Tag(ex.posture)
                    Tag(ex.zone)
                }
                Text(ex.text, fontSize = 15.sp, color = p.text2, lineHeight = 22.sp)
                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    ex.tips.forEach { Text("•  $it", fontSize = 14.sp, color = p.ink, lineHeight = 20.sp) }
                }
                Button(
                    onClick = { selected = null },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = p.gray, contentColor = p.ink),
                    shape = RoundedCornerShape(8.dp),
                ) { Text("Cerrar") }
            }
        }
    }
}

@Composable
fun Tag(text: String) {
    val p = LocalPal.current
    Surface(shape = RoundedCornerShape(6.dp), color = p.gray) {
        Text(
            text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = p.ink,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
        )
    }
}
