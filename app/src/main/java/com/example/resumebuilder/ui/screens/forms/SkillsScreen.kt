package com.example.resumebuilder.ui.screens.forms

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.resumebuilder.ui.components.FormField
import com.example.resumebuilder.ui.components.PrimaryButton
import com.example.resumebuilder.ui.components.ProgressHeader
import com.example.resumebuilder.ui.components.SectionCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillsScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    var input by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf(listOf<String>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Skills") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ProgressHeader(stepLabel = "Step 3 of 3", totalSteps = 3, currentStepIndex = 2)

            SectionCard("Add Skills") {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FormField("Skill", input, { input = it }, modifier = Modifier.weight(1f))
                    Button(
                        onClick = {
                            val t = input.trim()
                            if (t.isNotEmpty()) {
                                skills = (skills + t).distinct()
                                input = ""
                            }
                        },
                        modifier = Modifier.height(56.dp)
                    ) { Text("Add") }
                }
                Spacer(Modifier.height(12.dp))
                FlowRowMain(spacing = 8.dp, runSpacing = 8.dp) {
                    skills.forEach { s ->
                        AssistChip(
                            onClick = {},
                            label = { Text(s) },
                            trailingIcon = {
                                IconButton(onClick = { skills = skills - s }) { Icon(Icons.Default.Close, null) }
                            }
                        )
                    }
                }
            }

            PrimaryButton("Next", onClick = onNext)
        }
    }
}

/** Minimal FlowRow substitute without external libs */
@Composable
private fun FlowRowMain(
    spacing: androidx.compose.ui.unit.Dp,
    runSpacing: androidx.compose.ui.unit.Dp,
    content: @Composable () -> Unit
) {
    // For brevity: rely on Accompanist FlowRow if available. Placeholder shows chips in a Column.
    Column(verticalArrangement = Arrangement.spacedBy(runSpacing)) { content() }
}
