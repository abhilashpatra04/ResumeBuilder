package com.example.resumebuilder.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import com.example.resumebuilder.ui.components.PrimaryButton
import com.example.resumebuilder.ui.components.SecondaryButton
import java.io.File

@Composable
fun ExportSuccessScreen(
    onShare: () -> Unit,
    onCreateAnother: () -> Unit,
    onHome: () -> Unit,
    pdfPath: String? = null
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
            PrimaryButton("Share", onClick = {
                // If we have a PDF path, share it
                pdfPath?.let { path ->
                    val context = LocalContext.current
                    val file = File(path.replace("file://", ""))
                    if (file.exists()) {
                        val uri = FileProvider.getUriForFile(
                            context,
                            "com.example.resumebuilder.fileprovider",
                            file
                        )
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "application/pdf"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        val shareIntent = Intent.createChooser(intent, "Share Resume PDF")
                        startActivity(context, shareIntent, null)
                    }
                }
                onShare()
            })
            Spacer(Modifier.height(12.dp))
            SecondaryButton("Create Another Resume", onClick = onCreateAnother)
            Spacer(Modifier.height(12.dp))
            TextButton(onClick = onHome) { Text("Return Home") }
        }
    }
}
