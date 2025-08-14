package com.example.resumebuilder.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.resumebuilder.ui.components.PrimaryButton
import com.example.resumebuilder.ui.components.SecondaryButton

@Composable
fun WelcomeScreen(
    onCreateNew: () -> Unit,
    onMyResumes: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Build Beautiful, ATS‑Friendly Resumes", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(24.dp))
            PrimaryButton("Create New Resume", onClick = onCreateNew)
            Spacer(Modifier.height(12.dp))
            SecondaryButton("My Resumes", onClick = onMyResumes)
        }
    }
}
