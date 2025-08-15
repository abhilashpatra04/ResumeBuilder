// File: app/src/main/java/com/example/resumebuilder/ViewModels/ResumeViewModel.kt
package com.example.resumebuilder.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resumebuilder.data.repository.IResumeRepository
import com.example.resumebuilder.data.repository.RepositoryResult
import com.example.resumebuilder.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ResumeViewModel @Inject constructor(
    private val repository: IResumeRepository
) : ViewModel() {

    // Current resume being edited
    var currentResumeData by mutableStateOf(ResumeData())
        private set

    // UI State
    var uiState by mutableStateOf(ResumeUiState())
        private set

    // All resumes for list screen
    private val _allResumes = MutableStateFlow<RepositoryResult<List<ResumeData>>>(RepositoryResult.loading())
    val allResumes: StateFlow<RepositoryResult<List<ResumeData>>> = _allResumes.asStateFlow()

    // Current form step
    var currentStep by mutableStateOf(FormStep.PERSONAL_INFO)
        private set

    init {
        loadAllResumes()
    }

    // Navigation Methods
    fun navigateToStep(step: FormStep) {
        currentStep = step
    }

    fun nextStep() {
        currentStep = when (currentStep) {
            FormStep.PERSONAL_INFO -> FormStep.EXPERIENCE
            FormStep.EXPERIENCE -> FormStep.PROJECTS
            FormStep.PROJECTS -> FormStep.SKILLS
            FormStep.SKILLS -> FormStep.EDUCATION
            FormStep.EDUCATION -> FormStep.TEMPLATE_SELECTION
            FormStep.TEMPLATE_SELECTION -> FormStep.CUSTOMIZATION
            FormStep.CUSTOMIZATION -> FormStep.PREVIEW
            FormStep.PREVIEW -> FormStep.PREVIEW // Stay on preview
        }
    }

    fun previousStep() {
        currentStep = when (currentStep) {
            FormStep.PERSONAL_INFO -> FormStep.PERSONAL_INFO // Stay on first step
            FormStep.EXPERIENCE -> FormStep.PERSONAL_INFO
            FormStep.PROJECTS -> FormStep.EXPERIENCE
            FormStep.SKILLS -> FormStep.PROJECTS
            FormStep.EDUCATION -> FormStep.SKILLS
            FormStep.TEMPLATE_SELECTION -> FormStep.EDUCATION
            FormStep.CUSTOMIZATION -> FormStep.TEMPLATE_SELECTION
            FormStep.PREVIEW -> FormStep.CUSTOMIZATION
        }
    }

    // Resume CRUD Operations
    fun createNewResume(title: String = "New Resume") {
        currentResumeData = ResumeData(
            title = title,
            createdAt = Date(),
            updatedAt = Date()
        )
        currentStep = FormStep.PERSONAL_INFO
        updateUiState { copy(isLoading = false, error = null) }
    }

    fun loadResume(resumeId: Long) {
        viewModelScope.launch {
            updateUiState { copy(isLoading = true) }

            repository.getResumeById(resumeId)
                .onSuccess { resumeData ->
                    resumeData?.let {
                        currentResumeData = it
                        updateUiState { copy(isLoading = false, error = null) }
                    } ?: run {
                        updateUiState { copy(isLoading = false, error = "Resume not found") }
                    }
                }
                .onFailure { exception ->
                    updateUiState { copy(isLoading = false, error = exception.message ?: "Failed to load resume") }
                }
        }
    }

    fun saveResume() {
        viewModelScope.launch {
            updateUiState { copy(isLoading = true) }

            val result = if (currentResumeData.id == 0L) {
                repository.insertResume(currentResumeData)
            } else {
                repository.updateResume(currentResumeData).map { currentResumeData.id }
            }

            result
                .onSuccess { resumeId ->
                    if (currentResumeData.id == 0L) {
                        currentResumeData = currentResumeData.copy(id = resumeId)
                    }
                    updateUiState { copy(isLoading = false, error = null, saveStatus = SaveStatus.SAVED) }
                    loadAllResumes() // Refresh the list
                }
                .onFailure { exception ->
                    updateUiState { copy(isLoading = false, error = exception.message ?: "Failed to save resume") }
                }
        }
    }

    fun deleteResume(resumeId: Long) {
        viewModelScope.launch {
            updateUiState { copy(isLoading = true) }

            repository.deleteResume(resumeId)
                .onSuccess {
                    updateUiState { copy(isLoading = false, error = null) }
                    loadAllResumes() // Refresh the list
                }
                .onFailure { exception ->
                    updateUiState { copy(isLoading = false, error = exception.message ?: "Failed to delete resume") }
                }
        }
    }

    private fun loadAllResumes() {
        viewModelScope.launch {
            repository.getAllResumes()
                .collect { result ->
                    result
                        .onSuccess { resumes ->
                            _allResumes.value = RepositoryResult.success(resumes)
                        }
                        .onFailure { exception ->
                            _allResumes.value = RepositoryResult.error(exception.message ?: "Failed to load resumes")
                        }
                }
        }
    }

    // Personal Info Methods
    fun updatePersonalInfo(personalInfo: PersonalInfo) {
        currentResumeData = currentResumeData.copy(
            personalInfo = personalInfo,
            updatedAt = Date()
        )
        updateUiState { copy(saveStatus = SaveStatus.UNSAVED) }

        // Auto-save if resume exists
        if (currentResumeData.id != 0L) {
            viewModelScope.launch {
                repository.updatePersonalInfo(currentResumeData.id, personalInfo)
            }
        }
    }

    // Work Experience Methods
    fun addWorkExperience(experience: WorkExperience) {
        val experiences = currentResumeData.experiences.toMutableList()
        val newExperience = experience.copy(
            id = System.currentTimeMillis(), // Temporary ID
            resumeId = currentResumeData.id,
            sortOrder = experiences.size
        )
        experiences.add(newExperience)

        currentResumeData = currentResumeData.copy(
            experiences = experiences,
            updatedAt = Date()
        )
        updateUiState { copy(saveStatus = SaveStatus.UNSAVED) }

        // Auto-save to database if resume exists
        if (currentResumeData.id != 0L) {
            viewModelScope.launch {
                repository.addWorkExperience(currentResumeData.id, newExperience)
            }
        }
    }

    fun updateWorkExperience(experienceId: Long, experience: WorkExperience) {
        val experiences = currentResumeData.experiences.toMutableList()
        val index = experiences.indexOfFirst { it.id == experienceId }
        if (index != -1) {
            experiences[index] = experience.copy(id = experienceId)
            currentResumeData = currentResumeData.copy(
                experiences = experiences,
                updatedAt = Date()
            )
            updateUiState { copy(saveStatus = SaveStatus.UNSAVED) }

            // Auto-save to database if resume exists
            if (currentResumeData.id != 0L) {
                viewModelScope.launch {
                    repository.updateWorkExperience(experience.copy(id = experienceId))
                }
            }
        }
    }

    fun removeWorkExperience(experienceId: Long) {
        val experiences = currentResumeData.experiences.filterNot { it.id == experienceId }
        currentResumeData = currentResumeData.copy(
            experiences = experiences,
            updatedAt = Date()
        )
        updateUiState { copy(saveStatus = SaveStatus.UNSAVED) }

        // Delete from database if resume exists
        if (currentResumeData.id != 0L) {
            viewModelScope.launch {
                repository.deleteEducation(educationId)
            }
        }
    }

    // Template Methods
    fun selectTemplate(templateId: String) {
        currentResumeData = currentResumeData.copy(
            templateId = templateId,
            updatedAt = Date()
        )
        updateUiState { copy(saveStatus = SaveStatus.UNSAVED) }
    }

    // Search Methods
    fun searchResumes(query: String) {
        viewModelScope.launch {
            _allResumes.value = RepositoryResult.loading()

            repository.searchResumes(query)
                .onSuccess { resumes ->
                    _allResumes.value = RepositoryResult.success(resumes)
                }
                .onFailure { exception ->
                    _allResumes.value = RepositoryResult.error(exception.message ?: "Search failed")
                }
        }
    }

    // Validation Methods
    fun validateCurrentStep(): ValidationResult {
        return when (currentStep) {
            FormStep.PERSONAL_INFO -> validatePersonalInfo()
            FormStep.EXPERIENCE -> validateExperience()
            FormStep.PROJECTS -> validateProjects()
            FormStep.SKILLS -> validateSkills()
            FormStep.EDUCATION -> validateEducation()
            else -> ValidationResult(true, emptyList())
        }
    }

    private fun validatePersonalInfo(): ValidationResult {
        val errors = mutableListOf<String>()
        val info = currentResumeData.personalInfo

        if (info.fullName.isBlank()) errors.add("Full name is required")
        if (info.email.isBlank()) errors.add("Email is required")
        if (info.email.isNotBlank() && !info.email.contains("@")) errors.add("Valid email is required")
        if (info.jobTitle.isBlank()) errors.add("Job title is required")

        return ValidationResult(errors.isEmpty(), errors)
    }

    private fun validateExperience(): ValidationResult {
        val errors = mutableListOf<String>()

        if (currentResumeData.experiences.isEmpty() && currentResumeData.projects.isEmpty()) {
            errors.add("At least one work experience or project is required")
        }

        currentResumeData.experiences.forEachIndexed { index, experience ->
            if (experience.jobTitle.isBlank()) errors.add("Job title is required for experience ${index + 1}")
            if (experience.company.isBlank()) errors.add("Company is required for experience ${index + 1}")
        }

        return ValidationResult(errors.isEmpty(), errors)
    }

    private fun validateProjects(): ValidationResult {
        val errors = mutableListOf<String>()

        currentResumeData.projects.forEachIndexed { index, project ->
            if (project.name.isBlank()) errors.add("Project name is required for project ${index + 1}")
            if (project.description.isBlank()) errors.add("Project description is required for project ${index + 1}")
        }

        return ValidationResult(errors.isEmpty(), errors)
    }

    private fun validateSkills(): ValidationResult {
        val errors = mutableListOf<String>()

        if (currentResumeData.skills.isEmpty()) {
            errors.add("At least one skill is required")
        }

        return ValidationResult(errors.isEmpty(), errors)
    }

    private fun validateEducation(): ValidationResult {
        val errors = mutableListOf<String>()

        currentResumeData.education.forEachIndexed { index, education ->
            if (education.degree.isBlank()) errors.add("Degree is required for education ${index + 1}")
            if (education.institution.isBlank()) errors.add("Institution is required for education ${index + 1}")
        }

        return ValidationResult(errors.isEmpty(), errors)
    }

    // Utility Methods
    fun canProceedToNextStep(): Boolean {
        return validateCurrentStep().isValid
    }

    fun getStepProgress(): Float {
        return when (currentStep) {
            FormStep.PERSONAL_INFO -> 0.125f
            FormStep.EXPERIENCE -> 0.25f
            FormStep.PROJECTS -> 0.375f
            FormStep.SKILLS -> 0.5f
            FormStep.EDUCATION -> 0.625f
            FormStep.TEMPLATE_SELECTION -> 0.75f
            FormStep.CUSTOMIZATION -> 0.875f
            FormStep.PREVIEW -> 1f
        }
    }

    fun duplicateResume(resumeId: Long, newTitle: String) {
        viewModelScope.launch {
            updateUiState { copy(isLoading = true) }

            repository.duplicateResume(resumeId, newTitle)
                .onSuccess { newResumeId ->
                    updateUiState { copy(isLoading = false, error = null) }
                    loadAllResumes() // Refresh the list
                }
                .onFailure { exception ->
                    updateUiState { copy(isLoading = false, error = exception.message ?: "Failed to duplicate resume") }
                }
        }
    }

    // Error handling
    fun clearError() {
        updateUiState { copy(error = null) }
    }

    private fun updateUiState(update: ResumeUiState.() -> ResumeUiState) {
        uiState = uiState.update()
    }
}

// UI State Data Classes
data class ResumeUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val saveStatus: SaveStatus = SaveStatus.SAVED
)

enum class SaveStatus {
    SAVED,
    UNSAVED,
    SAVING,
    ERROR
}

enum class FormStep(val stepName: String, val stepNumber: Int) {
    PERSONAL_INFO("Personal Info", 1),
    EXPERIENCE("Experience", 2),
    PROJECTS("Projects", 3),
    SKILLS("Skills", 4),
    EDUCATION("Education", 5),
    TEMPLATE_SELECTION("Template", 6),
    CUSTOMIZATION("Customize", 7),
    PREVIEW("Preview", 8)
}

// Additional ViewModels for specific screens

@HiltViewModel
class ResumeListViewModel @Inject constructor(
    private val repository: IResumeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResumeListUiState())
    val uiState: StateFlow<ResumeListUiState> = _uiState.asStateFlow()

    private val _resumes = MutableStateFlow<List<ResumeData>>(emptyList())
    val resumes: StateFlow<List<ResumeData>> = _resumes.asStateFlow()

    init {
        loadResumes()
    }

    fun loadResumes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            repository.getAllResumes()
                .collect { result ->
                    result
                        .onSuccess { resumeList ->
                            _resumes.value = resumeList
                            _uiState.value = _uiState.value.copy(isLoading = false, error = null)
                        }
                        .onFailure { exception ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = exception.message ?: "Failed to load resumes"
                            )
                        }
                }
        }
    }

    fun searchResumes(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            repository.searchResumes(query)
                .onSuccess { searchResults ->
                    _resumes.value = searchResults
                    _uiState.value = _uiState.value.copy(isLoading = false, error = null)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Search failed"
                    )
                }
        }
    }

    fun deleteResume(resumeId: Long) {
        viewModelScope.launch {
            repository.deleteResume(resumeId)
                .onSuccess {
                    loadResumes() // Refresh the list
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Failed to delete resume"
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class ResumeListUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class TemplateViewModel @Inject constructor(
    private val repository: IResumeRepository
) : ViewModel() {

    private val _selectedTemplateId = MutableStateFlow("olivia")
    val selectedTemplateId: StateFlow<String> = _selectedTemplateId.asStateFlow()

    private val _availableTemplates = MutableStateFlow(getAvailableTemplates())
    val availableTemplates: StateFlow<List<TemplateConfig>> = _availableTemplates.asStateFlow()

    fun selectTemplate(templateId: String) {
        _selectedTemplateId.value = templateId
    }

    private fun getAvailableTemplates(): List<TemplateConfig> {
        return listOf(
            TemplateConfig(
                id = "olivia",
                name = "Olivia Wilson",
                description = "Clean single-column professional layout",
                category = TemplateCategory.PROFESSIONAL,
                colorScheme = ColorScheme.BLUE,
                layout = LayoutType.SINGLE_COLUMN,
                sectionsOrder = listOf(
                    SectionType.PERSONAL_INFO,
                    SectionType.SUMMARY,
                    SectionType.EXPERIENCE,
                    SectionType.PROJECTS,
                    SectionType.SKILLS,
                    SectionType.EDUCATION
                )
            ),
            TemplateConfig(
                id = "sarah",
                name = "Sarah Amelia",
                description = "Modern sidebar design with visual hierarchy",
                category = TemplateCategory.MODERN,
                colorScheme = ColorScheme.GREEN,
                layout = LayoutType.SIDEBAR,
                sectionsOrder = listOf(
                    SectionType.PERSONAL_INFO,
                    SectionType.SUMMARY,
                    SectionType.EXPERIENCE,
                    SectionType.PROJECTS
                )
            ),
            TemplateConfig(
                id = "michael",
                name = "Michael Chen",
                description = "Executive format with emphasis on experience",
                category = TemplateCategory.EXECUTIVE,
                colorScheme = ColorScheme.BLACK,
                layout = LayoutType.SINGLE_COLUMN,
                sectionsOrder = listOf(
                    SectionType.PERSONAL_INFO,
                    SectionType.SUMMARY,
                    SectionType.EXPERIENCE,
                    SectionType.SKILLS,
                    SectionType.EDUCATION
                )
            ),
            TemplateConfig(
                id = "emily",
                name = "Emily Rodriguez",
                description = "Creative layout with skill highlights",
                category = TemplateCategory.CREATIVE,
                colorScheme = ColorScheme.PURPLE,
                layout = LayoutType.TWO_COLUMN,
                sectionsOrder = listOf(
                    SectionType.PERSONAL_INFO,
                    SectionType.SKILLS,
                    SectionType.EXPERIENCE,
                    SectionType.PROJECTS,
                    SectionType.EDUCATION
                )
            ),
            TemplateConfig(
                id = "david",
                name = "David Kumar",
                description = "Technical resume with project emphasis",
                category = TemplateCategory.TECHNICAL,
                colorScheme = ColorScheme.ORANGE,
                layout = LayoutType.SINGLE_COLUMN,
                sectionsOrder = listOf(
                    SectionType.PERSONAL_INFO,
                    SectionType.SUMMARY,
                    SectionType.PROJECTS,
                    SectionType.EXPERIENCE,
                    SectionType.SKILLS,
                    SectionType.EDUCATION
                )
            )
        )
        viewModelScope.launch {
            repository.deleteWorkExperience(experienceId)
        }
    }
}



// Project Methods
fun addProject(project: Project) {
    val projects = currentResumeData.projects.toMutableList()
    val newProject = project.copy(
        id = System.currentTimeMillis(), // Temporary ID
        resumeId = currentResumeData.id,
        sortOrder = projects.size
    )
    projects.add(newProject)

    currentResumeData = currentResumeData.copy(
        projects = projects,
        updatedAt = Date()
    )
    updateUiState { copy(saveStatus = SaveStatus.UNSAVED) }

    // Auto-save to database if resume exists
    if (currentResumeData.id != 0L) {
        viewModelScope.launch {
            repository.addProject(currentResumeData.id, newProject)
        }
    }
}

fun updateProject(projectId: Long, project: Project) {
    val projects = currentResumeData.projects.toMutableList()
    val index = projects.indexOfFirst { it.id == projectId }
    if (index != -1) {
        projects[index] = project.copy(id = projectId)
        currentResumeData = currentResumeData.copy(
            projects = projects,
            updatedAt = Date()
        )
        updateUiState { copy(saveStatus = SaveStatus.UNSAVED) }

        // Auto-save to database if resume exists
        if (currentResumeData.id != 0L) {
            viewModelScope.launch {
                repository.updateProject(project.copy(id = projectId))
            }
        }
    }
}

fun removeProject(projectId: Long) {
    val projects = currentResumeData.projects.filterNot { it.id == projectId }
    currentResumeData = currentResumeData.copy(
        projects = projects,
        updatedAt = Date()
    )
    updateUiState { copy(saveStatus = SaveStatus.UNSAVED) }

    // Delete from database if resume exists
    if (currentResumeData.id != 0L) {
        viewModelScope.launch {
            repository.deleteProject(projectId)
        }
    }
}

// Skills Methods
fun addSkill(skill: Skill) {
    val skills = currentResumeData.skills.toMutableList()
    val newSkill = skill.copy(
        id = System.currentTimeMillis(), // Temporary ID
        resumeId = currentResumeData.id,
        sortOrder = skills.size
    )
    skills.add(newSkill)

    currentResumeData = currentResumeData.copy(
        skills = skills,
        updatedAt = Date()
    )
    updateUiState { copy(saveStatus = SaveStatus.UNSAVED) }

    // Auto-save to database if resume exists
    if (currentResumeData.id != 0L) {
        viewModelScope.launch {
            repository.addSkill(currentResumeData.id, newSkill)
        }
    }
}

fun removeSkill(skillId: Long) {
    val skills = currentResumeData.skills.filterNot { it.id == skillId }
    currentResumeData = currentResumeData.copy(
        skills = skills,
        updatedAt = Date()
    )
    updateUiState { copy(saveStatus = SaveStatus.UNSAVED) }

    // Delete from database if resume exists
    if (currentResumeData.id != 0L) {
        viewModelScope.launch {
            repository.deleteSkill(skillId)
        }
    }
}

// Education Methods
fun addEducation(education: Education) {
    val educationList = currentResumeData.education.toMutableList()
    val newEducation = education.copy(
        id = System.currentTimeMillis(), // Temporary ID
        resumeId = currentResumeData.id,
        sortOrder = educationList.size
    )
    educationList.add(newEducation)

    currentResumeData = currentResumeData.copy(
        education = educationList,
        updatedAt = Date()
    )
    updateUiState { copy(saveStatus = SaveStatus.UNSAVED) }

    // Auto-save to database if resume exists
    if (currentResumeData.id != 0L) {
        viewModelScope.launch {
            repository.addEducation(currentResumeData.id, newEducation)
        }
    }
}

fun removeEducation(educationId: Long) {
    val educationList = currentResumeData.education.filterNot { it.id == educationId }
    currentResumeData = currentResumeData.copy(
        education = educationList,
        updatedAt = Date()
    )
    updateUiState { copy(saveStatus = SaveStatus.UNSAVED) }

    // Delete from database if resume exists
    if (currentResumeData.id != 0L) {
        viewModelScope.launch {
            repository.addEducation(currentResumeData.id, newEducation)
        }
    }
}