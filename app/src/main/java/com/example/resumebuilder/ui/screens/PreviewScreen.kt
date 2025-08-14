package com.example.resumebuilder.ui.screens

// import androidx.compose.material3.* // Wildcard import removed
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.resumebuilder.ui.components.PrimaryButton


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewScreen(
    onBack: () -> Unit,
    onExport: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Preview (A4)") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 2.dp) {
                Column(Modifier.padding(16.dp)) {
                    PrimaryButton("Export PDF", onClick = onExport)
                }
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            // Placeholder preview box with A4 ratio (approx)
            Surface(
                tonalElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f / 1.4142f) // A4 ratio ~ 1 : √2
            ) {
                // TODO: render the chosen template with ResumeData here
                Box(Modifier.padding(16.dp)) { Text("A4 Template Preview") }
            }
            Spacer(Modifier.height(12.dp))
            Text("Zoom & scroll coming in Phase 2.", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
