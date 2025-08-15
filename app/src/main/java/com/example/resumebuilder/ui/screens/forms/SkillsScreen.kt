package com.example.resumebuilder.ui.screens.forms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.resumebuilder.ViewModels.ResumeViewModel
import com.example.resumebuilder.model.Skill
import com.example.resumebuilder.ui.components.FormField
import com.example.resumebuilder.ui.components.PrimaryButton
import com.example.resumebuilder.ui.components.ProgressHeader
import com.example.resumebuilder.ui.components.SectionCard
import com.example.resumebuilder.ui.theme.ResumeText
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillsScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    viewModel: ResumeViewModel = hiltViewModel()
) {
    val resumeData by viewModel.resumeData.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var currentSkill by remember { mutableStateOf<Skill?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Skills") },
                navigationIcon = { 
                    IconButton(onClick = onBack) { 
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null) 
                    } 
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    currentSkill = null
                    showAddDialog = true 
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Skill")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProgressHeader(stepLabel = "Step 3 of 3", totalSteps = 3, currentStepIndex = 2)
            
            if (resumeData.skills.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Tap the + button to add skills",
                        style = ResumeText.Muted
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(resumeData.skills) { skill ->
                        SkillItem(
                            skill = skill,
                            onEdit = {
                                currentSkill = skill
                                showAddDialog = true
                            },
                            onDelete = { viewModel.removeSkill(skill.id) }
                        )
                    }
                    item { Spacer(Modifier.height(72.dp)) }
                }
            }
            
            PrimaryButton(
                text = "Save & Continue",
                onClick = onNext,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        if (showAddDialog) {
            SkillDialog(
                skill = currentSkill,
                onDismiss = { showAddDialog = false },
                onSave = { skill ->
                    if (currentSkill != null) {
                        viewModel.updateSkill(skill)
                    } else {
                        viewModel.addSkill(skill)
                    }
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun SkillItem(
    skill: Skill,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    SectionCard(
        title = skill.name,
        trailing = {
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    ) {
        // Display skill level as a slider or stars
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Proficiency: ", style = ResumeText.Muted)
            Spacer(Modifier.width(8.dp))
            LinearProgressIndicator(
                progress = { skill.level / 5f },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Text("${skill.level}/5", style = ResumeText.Muted)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillDialog(
    skill: Skill?,
    onDismiss: () -> Unit,
    onSave: (Skill) -> Unit
) {
    val isEditing = skill != null
    var name by remember { mutableStateOf(skill?.name ?: "") }
    var level by remember { mutableStateOf(skill?.level ?: 3) }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isEditing) "Edit Skill" else "Add Skill",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(16.dp))
                
                FormField(
                    label = "Skill Name",
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "e.g. Java Programming"
                )
                Spacer(Modifier.height(16.dp))
                
                Text("Proficiency Level: $level/5")
                Slider(
                    value = level.toFloat(),
                    onValueChange = { level = it.toInt() },
                    valueRange = 1f..5f,
                    steps = 3
                )
                
                Spacer(Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val newSkill = Skill(
                                id = skill?.id ?: UUID.randomUUID().toString(),
                                name = name,
                                level = level
                            )
                            onSave(newSkill)
                        },
                        enabled = name.isNotBlank(),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
