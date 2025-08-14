package com.example.resumebuilder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.resumebuilder.ui.components.PrimaryButton
import com.example.resumebuilder.ui.components.SecondaryButton

@Composable
fun ExportSuccessScreen(
    onShare: () -> Unit,
    onCreateAnother: () -> Unit,
    onHome: () -> Unit
) {
    Scaffold { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(64.dp))
            Spacer(Modifier.height(16.dp))
            Text("Your resume has been saved to Downloads", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(24.dp))
            PrimaryButton("Share", onClick = onShare)
            Spacer(Modifier.height(12.dp))
            SecondaryButton("Create Another Resume", onClick = onCreateAnother)
            Spacer(Modifier.height(12.dp))
            TextButton(onClick = onHome) { Text("Return Home") }
        }
    }
}
