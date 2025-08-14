package com.example.resumebuilder.ui.screens.forms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.resumebuilder.ui.components.FormField
import com.example.resumebuilder.ui.components.PrimaryButton
import com.example.resumebuilder.ui.components.ProgressHeader
import com.example.resumebuilder.ui.components.SectionCard

data class ExperienceUi(
    var title: String = "",
    var company: String = "",
    var start: String = "",
    var end: String = "",
    var bullets: String = "" // comma-separated for Phase 1 simplicity
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExperienceScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    var items by remember { mutableStateOf(mutableListOf(ExperienceUi())) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Work Experience") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { items = (items + ExperienceUi()).toMutableList() },
                text = { Text("Add Experience") },
                icon = { Icon(Icons.Default.Add, null) }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            ProgressHeader(stepLabel = "Step 2 of 3", totalSteps = 3, currentStepIndex = 1)
            Spacer(Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                itemsIndexed(items) { index, exp ->
                    SectionCard("Role ${index + 1}", trailing = {
                        if (items.size > 1) IconButton(onClick = {
                            items = items.toMutableList().also { it.removeAt(index) }
                        }) { Icon(Icons.Default.Delete, null) }
                    }) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            FormField("Job Title", exp.title, { exp.title = it })
                            FormField("Company", exp.company, { exp.company = it })
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                FormField("Start", exp.start, { exp.start = it }, modifier = Modifier.weight(1f))
                                FormField("End", exp.end, { exp.end = it }, modifier = Modifier.weight(1f))
                            }
                            FormField("Bullets (comma separated)", exp.bullets, { exp.bullets = it }, "Led X..., Improved Y...")
                        }
                    }
                }
                item { Spacer(Modifier.height(72.dp)) }
            }

            PrimaryButton("Next", onClick = onNext, modifier = Modifier.fillMaxWidth())
        }
    }
}
