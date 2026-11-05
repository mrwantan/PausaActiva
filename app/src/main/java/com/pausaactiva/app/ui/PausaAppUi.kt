@file:OptIn(ExperimentalMaterial3Api::class)

package com.pausaactiva.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun PausaAppUi(vm: AppViewModel, act: SystemActions) {
    val p = LocalPal.current
    val ui by vm.ui.collectAsState()
    val session by vm.session.collectAsState()
    var tab by rememberSaveable { mutableIntStateOf(0) }

    Box(Modifier.fillMaxSize().background(p.bg)) {
        Scaffold(
            containerColor = p.bg,
            bottomBar = {
                NavigationBar(containerColor = p.bg) {
                    val colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = p.ink, selectedTextColor = p.ink,
                        unselectedIconColor = p.text2, unselectedTextColor = p.text2,
                        indicatorColor = p.gray,
                    )
                    NavigationBarItem(
                        selected = tab == 0, onClick = { tab = 0 }, colors = colors,
                        icon = { Icon(Icons.Filled.Home, contentDescription = null) }, label = { Text("Inicio") },
                    )
                    NavigationBarItem(
                        selected = tab == 1, onClick = { tab = 1 }, colors = colors,
                        icon = { Icon(Icons.Filled.SelfImprovement, contentDescription = null) }, label = { Text("Ejercicios") },
                    )
                    NavigationBarItem(
                        selected = tab == 2, onClick = { tab = 2 }, colors = colors,
                        icon = { Icon(Icons.Filled.Settings, contentDescription = null) }, label = { Text("Ajustes") },
                    )
                }
            },
        ) { pad ->
            Box(Modifier.padding(pad).statusBarsPadding()) {
                when (tab) {
                    0 -> HomeScreen(ui, vm, act)
                    1 -> ExercisesScreen()
                    else -> SettingsScreen(ui, vm, act)
                }
            }
        }

        if (!ui.onboarded) OnboardingScreen(ui, vm, act)
        session?.let { SessionScreen(it, vm) }
    }
}
