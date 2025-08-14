package com.example.resumebuilder.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.resumebuilder.R
import com.example.resumebuilder.ui.components.PrimaryButton

data class TemplateUi(val id: String, val title: String, val preview: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplatePickerScreen(
    onBack: () -> Unit,
    onSelectTemplate: (String) -> Unit
) {
    val templates = listOf(
        TemplateUi("olivia", "Olivia (One‑Column)", R.drawable.ic_template_one),
        TemplateUi("sarah", "Sarah (Sidebar)", R.drawable.ic_template_two)
    )
    var selected by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Choose Template") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 2.dp) {
                Column(Modifier.padding(16.dp)) {
                    PrimaryButton(
                        text = "Customize",
                        onClick = { selected?.let(onSelectTemplate) },
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
                    onClick = { selected = t.id },
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = if (selected == t.id) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Image(painterResource(id = t.preview), contentDescription = null, modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp))
                        Spacer(Modifier.height(8.dp))
                        Text(t.title, style = MaterialTheme.typography.titleMedium)
                        if (selected == t.id) {
                            Spacer(Modifier.height(4.dp)); Text("Selected", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}
