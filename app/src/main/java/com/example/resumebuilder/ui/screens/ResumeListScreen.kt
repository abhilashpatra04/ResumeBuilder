package com.example.resumebuilder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.resumebuilder.ui.components.PrimaryButton
import com.example.resumebuilder.ui.theme.ResumeText

data class ResumeListItemUi(val id: String, val title: String, val updatedAt: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumeListScreen(
    onBack: () -> Unit,
    onCreateNew: () -> Unit,
    onOpenResume: (String) -> Unit
) {
    // Placeholder list until wired to Room
    val items by remember {
        mutableStateOf(
            listOf(
                ResumeListItemUi("1", "Abhilash – SWE Resume", "Edited today"),
                ResumeListItemUi("2", "Abhilash – Data Resume", "Edited 2 days ago")
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Resumes") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) } }
            )
        },
        floatingActionButton = { FloatingActionButton(onClick = onCreateNew) { Icon(Icons.Default.Add, null) } }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            if (items.isEmpty()) {
                Text("No resumes yet.", style = ResumeText.Muted)
                Spacer(Modifier.height(12.dp))
                PrimaryButton("Create New Resume", onClick = onCreateNew)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(items) { item ->
                        ElevatedCard(
                            onClick = { onOpenResume(item.id) }
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(item.title, style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(4.dp))
                                Text(item.updatedAt, style = ResumeText.Muted)
                            }
                        }
                    }
                }
            }
        }
    }
}
