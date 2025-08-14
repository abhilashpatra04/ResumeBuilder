package com.example.resumebuilder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.resumebuilder.ui.components.PrimaryButton
import com.example.resumebuilder.ui.components.SectionCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumeBuilderScreen(
    onBack: () -> Unit,
    onPersonalInfo: () -> Unit,
    onExperience: () -> Unit,
    onSkills: () -> Unit,
    onNextToTemplates: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resume Builder") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            SectionCard("Personal Info") {
                Text("Name, title, contact details.")
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = onPersonalInfo) { Text("Edit") }
            }
            SectionCard("Work Experience") {
                Text("Add roles, dates, and bullet points.")
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = onExperience) { Text("Edit") }
            }
            SectionCard("Skills") {
                Text("Add and arrange your skills.")
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = onSkills) { Text("Edit") }
            }
            Spacer(Modifier.height(8.dp))
            PrimaryButton("Choose Template", onClick = onNextToTemplates)
        }
    }
}
