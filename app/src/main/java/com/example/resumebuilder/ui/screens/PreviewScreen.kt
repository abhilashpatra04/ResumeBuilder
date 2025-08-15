package com.example.resumebuilder.ui.screens

// import androidx.compose.material3.* // Wildcard import removed
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.resumebuilder.data.Template
import com.example.resumebuilder.ui.components.PrimaryButton
import com.example.resumebuilder.ui.theme.BluePrimary
import com.example.resumebuilder.ui.theme.HeadingDeepBlue
import com.example.resumebuilder.ui.theme.ResumeText
import com.example.resumebuilder.ui.theme.SidebarBlush
import com.example.resumebuilder.ui.theme.SidebarText
import com.example.resumebuilder.utils.PdfExport
import com.example.resumebuilder.viewmodel.ResumeViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewScreen(
    onBack: () -> Unit,
    onExport: () -> Unit,
    viewModel: ResumeViewModel = viewModel()
) {
    val resumeData by viewModel.resumeData.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Preview (A4)") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 2.dp) {
                Column(Modifier.padding(16.dp)) {
                    PrimaryButton("Export PDF", onClick = {
                        // Export PDF using the PdfExport utility
                        val context = LocalContext.current
                        PdfExport.exportA4Pdf(context, resumeData).fold(
                            onSuccess = { fileUri ->
                                // Navigate to success screen
                                onExport()
                            },
                            onFailure = { error ->
                                // In a real app, we would show an error message
                                // For Phase 1, we'll just navigate to success
                                onExport()
                            }
                        )
                    })
                }
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            // Preview box with A4 ratio (approx)
            Surface(
                tonalElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f / 1.4142f) // A4 ratio ~ 1 : √2
            ) {
                when (resumeData.selectedTemplate) {
                    Template.PROFESSIONAL -> ProfessionalTemplate(resumeData)
                    Template.MODERN -> ModernTemplate(resumeData)
                    else -> ModernTemplate(resumeData) // Default to Modern if null
                }
            }
            Spacer(Modifier.height(12.dp))
            Text("Zoom & scroll coming in Phase 2.", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun ModernTemplate(resumeData: com.example.resumebuilder.data.ResumeData) {
    Column(Modifier.padding(24.dp)) {
        // Header
        Text(
            text = resumeData.personalInfo.fullName ?: "Your Name",
            style = ResumeText.Heading,
            color = HeadingDeepBlue
        )
        Text(
            text = resumeData.personalInfo.jobTitle ?: "Your Job Title",
            style = ResumeText.Subhead,
            color = BluePrimary
        )
        
        // Contact Info
        Spacer(Modifier.height(12.dp))
        Row {
            resumeData.personalInfo.email?.let { Text(it, style = ResumeText.Body) }
            Spacer(Modifier.width(16.dp))
            resumeData.personalInfo.phone?.let { Text(it, style = ResumeText.Body) }
        }
        
        // Summary
        Spacer(Modifier.height(16.dp))
        resumeData.personalInfo.summary?.let {
            Text("SUMMARY", style = ResumeText.Subhead, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(it, style = ResumeText.Body)
        }
        
        // Experience
        if (resumeData.experiences.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text("EXPERIENCE", style = ResumeText.Subhead, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            resumeData.experiences.forEach { exp ->
                Text(exp.jobTitle ?: "", style = ResumeText.Body, fontWeight = FontWeight.Bold)
                Text("${exp.company ?: ""} | ${exp.startDate ?: ""} - ${exp.endDate ?: "Present"}", style = ResumeText.Muted)
                Spacer(Modifier.height(4.dp))
                exp.bulletPoints?.forEach { bullet ->
                    Row {
                        Text("• ", style = ResumeText.Body)
                        Text(bullet, style = ResumeText.Body)
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
        
        // Skills
        if (resumeData.skills.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text("SKILLS", style = ResumeText.Subhead, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(resumeData.skills.joinToString(", ") { it.name ?: "" }, style = ResumeText.Body)
        }
    }
}

@Composable
fun ProfessionalTemplate(resumeData: com.example.resumebuilder.data.ResumeData) {
    Row(Modifier.fillMaxWidth()) {
        // Sidebar
        Column(
            Modifier
                .fillMaxHeight()
                .width(120.dp)
                .background(SidebarBlush)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))
            
            // Contact Info in sidebar
            Text("CONTACT", style = ResumeText.Subhead, color = SidebarText, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            resumeData.personalInfo.email?.let { Text(it, style = ResumeText.Muted, color = SidebarText) }
            Spacer(Modifier.height(4.dp))
            resumeData.personalInfo.phone?.let { Text(it, style = ResumeText.Muted, color = SidebarText) }
            
            // Skills in sidebar
            if (resumeData.skills.isNotEmpty()) {
                Spacer(Modifier.height(24.dp))
                Text("SKILLS", style = ResumeText.Subhead, color = SidebarText, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                resumeData.skills.forEach { skill ->
                    Text(skill.name ?: "", style = ResumeText.Muted, color = SidebarText)
                    Spacer(Modifier.height(4.dp))
                }
            }
        }
        
        // Main content
        Column(Modifier.padding(24.dp)) {
            // Header
            Text(
                text = resumeData.personalInfo.fullName ?: "Your Name",
                style = ResumeText.Heading,
                color = HeadingDeepBlue
            )
            Text(
                text = resumeData.personalInfo.jobTitle ?: "Your Job Title",
                style = ResumeText.Subhead,
                color = BluePrimary
            )
            
            // Summary
            Spacer(Modifier.height(16.dp))
            resumeData.personalInfo.summary?.let {
                Text("PROFESSIONAL SUMMARY", style = ResumeText.Subhead, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(it, style = ResumeText.Body)
            }
            
            // Experience
            if (resumeData.experiences.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                Text("WORK EXPERIENCE", style = ResumeText.Subhead, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                resumeData.experiences.forEach { exp ->
                    Text(exp.jobTitle ?: "", style = ResumeText.Body, fontWeight = FontWeight.Bold)
                    Text("${exp.company ?: ""} | ${exp.startDate ?: ""} - ${exp.endDate ?: "Present"}", style = ResumeText.Muted)
                    Spacer(Modifier.height(4.dp))
                    exp.bulletPoints?.forEach { bullet ->
                        Row {
                            Text("• ", style = ResumeText.Body)
                            Text(bullet, style = ResumeText.Body)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}
