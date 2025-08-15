package com.example.resumebuilder.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.resumebuilder.R
import com.example.resumebuilder.ViewModels.ResumeViewModel
import com.example.resumebuilder.model.Template
import com.example.resumebuilder.ui.components.PrimaryButton

data class TemplateUi(val template: Template, val title: String, val preview: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplatePickerScreen(
    onBack: () -> Unit,
    onSelectTemplate: (String) -> Unit,
    viewModel: ResumeViewModel = viewModel()
) {
    val templates = listOf(
        TemplateUi(Template.MODERN, "Olivia (One‑Column)", R.drawable.ic_template_one),
        TemplateUi(Template.PROFESSIONAL, "Sarah (Sidebar)", R.drawable.ic_template_two)
    )
    
    val resumeData by viewModel.resumeData.collectAsState()
    var selected by remember { mutableStateOf(resumeData.selectedTemplate) }
    
    LaunchedEffect(selected) {
        selected?.let { viewModel.selectTemplate(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Choose Template") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        },
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Surface(tonalElevation = 2.dp) {
                Column(Modifier.padding(16.dp)) {
                    PrimaryButton(
                        text = "Customize",
                        onClick = { onSelectTemplate(selected?.name ?: Template.MODERN.name) },
                        enabled = selected != null
                    )
                }
            }
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(padding)
        ) {
            items(templates) { t ->
                ElevatedCard(
                    onClick = { selected = t.template },
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = if (selected == t.template) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Image(painterResource(id = t.preview), contentDescription = null, modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp))
                        Spacer(Modifier.height(8.dp))
                        Text(t.title, style = MaterialTheme.typography.titleMedium)
                        if (selected == t.template) {
                            Spacer(Modifier.height(4.dp)); Text("Selected", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}
