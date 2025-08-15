package com.example.resumebuilder.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.resumebuilder.ViewModels.ResumeViewModel
import com.example.resumebuilder.ui.components.PrimaryButton
import com.example.resumebuilder.ui.components.SectionCard
import com.example.resumebuilder.ui.theme.HeadingDeepBlue
import com.example.resumebuilder.ui.theme.ResumeText
import com.example.resumebuilder.ui.theme.TextPrimary


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizeScreen(
    onBack: () -> Unit,
    onPreview: () -> Unit,
    viewModel: ResumeViewModel = viewModel()
) {
    // For Phase 1, we'll keep these as local state
    // In Phase 2, these would be stored in the ViewModel
    var headingSize by remember { mutableStateOf(16f) }
    var bodySize by remember { mutableStateOf(12f) }
    var boldHeadings by remember { mutableStateOf(true) }
    // Simple color toggles for Phase 1
    var headingColor by remember { mutableStateOf(HeadingDeepBlue) }
    var bodyColor by remember { mutableStateOf(TextPrimary) }
    
    // Get the current resume data to check if a template is selected
    val resumeData by viewModel.resumeData.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customize") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 2.dp) {
                Column(Modifier.padding(16.dp)) {
                    PrimaryButton("Preview", onClick = onPreview)
                }
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            SectionCard("Typography") {
                Text("Headings", style = ResumeText.Subhead)
                Slider(value = headingSize, onValueChange = { headingSize = it }, valueRange = 14f..20f)
                Spacer(Modifier.height(8.dp))
                Row {
                    Checkbox(checked = boldHeadings, onCheckedChange = { boldHeadings = it })
                    Spacer(Modifier.width(8.dp)); Text("Bold headings")
                }
                Spacer(Modifier.height(16.dp))
                Text("Body", style = ResumeText.Subhead)
                Slider(value = bodySize, onValueChange = { bodySize = it }, valueRange = 10f..14f)
            }

            SectionCard("Colors") {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ElevatedButton(onClick = { /* open color picker */ }) { Text("Pick Heading Color") }
                    ElevatedButton(onClick = { /* open color picker */ }) { Text("Pick Body Color") }
                }
                Spacer(Modifier.height(8.dp))
                Text("For Phase 1 we use sensible defaults; full picker arrives in Phase 2.", style = ResumeText.Muted)
            }
        }
    }
}
