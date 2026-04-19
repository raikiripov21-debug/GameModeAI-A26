package com.gamemodeai.a06.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen(hasOverlayPermission: () -> Boolean, hasUsagePermission: () -> Boolean,
    onRequestOverlay: () -> Unit, onRequestUsage: () -> Unit,
    onStartService: () -> Unit, onStopService: () -> Unit) {
    var running by remember { mutableStateOf(false) }
    Scaffold { pad ->
        Column(Modifier.fillMaxSize().padding(pad).padding(16.dp).verticalScroll(rememberScrollState()),
            Arrangement.spacedBy(16.dp), Alignment.CenterHorizontally) {
            Text("GameMode AI", style = MaterialTheme.typography.headlineMedium)
            Text("Samsung A06 — Optimizes system when Free Fire is active.", style = MaterialTheme.typography.bodyMedium)
            HorizontalDivider()
            PermCard("Overlay Permission", hasOverlayPermission(), onRequestOverlay)
            PermCard("Usage Stats Permission", hasUsagePermission(), onRequestUsage)
            HorizontalDivider()
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { onStartService(); running = true }, enabled = !running) { Text("Start") }
                OutlinedButton(onClick = { onStopService(); running = false }, enabled = running) { Text("Stop") }
            }
            if (running) Text("Monitoring for Free Fire...", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun PermCard(title: String, granted: Boolean, onRequest: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), Alignment.CenterVertically, Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall)
                Text(if (granted) "Granted" else "Required", style = MaterialTheme.typography.bodySmall,
                    color = if (granted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
            }
            if (!granted) TextButton(onClick = onRequest) { Text("Grant") }
        }
    }
}
