// File: app/src/main/java/com/example/resumebuilder/data/repository/ResumeRepository.kt
package com.example.resumebuilder.data.repository

import com.example.resumebuilder.data.local.ResumeDao
import com.example.resumebuilder.data.local.entities.*
import com.example.resumebuilder.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

// Repository Interface for testing and abstraction
interface IResumeRepository {
    fun getAllResumes(): Flow<Result<List<ResumeData>>>
    suspend fun getResumeById(resumeId: Long): Result<ResumeData?>
    suspend fun insertResume(resumeData: ResumeData): Result<Long>
    suspend fun updateResume(resumeData: ResumeData): Result<Unit>
    suspend fun deleteResume(resumeId: Long): Result<Unit>
    suspend fun searchResumes(query: String): Result<List<ResumeData>>

    // Individual section operations
    suspend fun updatePersonalInfo(resumeId: Long, personalInfo: PersonalInfo): Result<Unit>
    suspend fun addWorkExperience(resumeId: Long, experience: WorkExperience): Result<Long>
    suspend fun updateWorkExperience(experience: WorkExperience): Result<Unit>
    suspend fun deleteWorkExperience(experienceId: Long): Result<Unit>

    suspend fun addEducation(resumeId: Long, education: Education): Result<Long>
    suspend fun updateEducation(education: Education): Result<Unit>
    suspend fun deleteEducation(educationId: Long): Result<Unit>

    suspend fun addSkill(resumeId: Long, skill: Skill): Result<Long>
    suspend fun updateSkill(skill: Skill): Result<Unit>
    suspend fun deleteSkill(skillId: Long): Result<Unit>

    suspend fun addProject(resumeId: Long, project: Project): Result<Long>
    suspend fun updateProject(project: Project): Result<Unit>
    suspend fun deleteProject(projectId: Long): Result<Unit>

    // Analytics and utility
    suspend fun getResumeCount(): Result<Int>
    suspend fun getCompletedResumeCount(): Result<Int>
    suspend fun getUsedTemplates(): Result<List<String>>
}

@Singleton
class ResumeRepository @Inject constructor(
    private val resumeDao: ResumeDao
) : IResumeRepository {

    override fun getAllResumes(): Flow<Result<List<ResumeData>>> {
        return resumeDao.getAllCompleteResumes()
            .map { entities ->
                Result.success(entities.map { it.toModel() })
            }
            .catch { exception ->
                emit(Result.failure(RepositoryException("Failed to fetch resumes", exception)))
            }
    }

    override suspend fun getResumeById(resumeId: Long): Result<ResumeData?> {
        return try {
            val entity = resumeDao.getCompleteResumeById(resumeId)
            Result.success(entity?.toModel())
        } catch (exception: Exception) {
            Result.failure(RepositoryException("Failed to fetch resume with id $resumeId", exception))
        }
    }

    override suspend fun insertResume(resumeData: ResumeData): Result<Long> {
        return try {
            // Validate resume data before insertion
            val validation = resumeData.validate()
            if (!validation.isValid) {
                return Result.failure(ValidationException("Resume validation failed", validation.errors))
            }

            val resumeId = resumeDao.insertCompleteResume(resumeData)
            Result.success(resumeId)
        } catch (exception: Exception) {
            Result.failure(RepositoryException("Failed to insert resume", exception))
        }
    }

    override suspend fun updateResume(resumeData: ResumeData): Result<Unit> {
        return try {
            // Validate resume data before update
            val validation = resumeData.validate()
            if (!validation.isValid) {
                return Result.failure(ValidationException("Resume validation failed", validation.errors))
            }

            resumeDao.updateCompleteResume(resumeData)
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(RepositoryException("Failed to update resume", exception))
        }
    }

    override suspend fun deleteResume(resumeId: Long): Result<Unit> {
        return try {
            resumeDao.deleteCompleteResume(resumeId)
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(RepositoryException("Failed to delete resume", exception))
        }
    }

    override suspend fun searchResumes(query: String): Result<List<ResumeData>> {
        return try {
            if (query.isBlank()) {
                return Result.success(emptyList())
            }

            val entities = resumeDao.searchResumes(query)
            val resumeData = mutableListOf<ResumeData>()

            entities.forEach { entity ->
                val completeResume = resumeDao.getCompleteResumeById(entity.id)
                completeResume?.let { resumeData.add(it.toModel()) }
            }

            Result.success(resumeData)
        } catch (exception: Exception) {
            Result.failure(RepositoryException("Failed to search resumes", exception))
        }
    }

    // Personal Info Operations
    override suspend fun updatePersonalInfo(resumeId: Long, personalInfo: PersonalInfo): Result<Unit> {
        return try {
            // Validate email format
            if (personalInfo.email.isNotBlank() && !personalInfo.email.contains("@")) {
                return Result.failure(ValidationException("Invalid email format", listOf("Email must contain @")))
            }

            val resume = resumeDao.getResumeById(resumeId)
                ?: return Result.failure(RepositoryException("Resume not found", null))

            val updatedResume = resume.copy(
                personalInfo = PersonalInfoEmbedded.fromModel(personalInfo),
                updatedAt = Date()
            )

            resumeDao.updateResume(updatedResume)
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(RepositoryException("Failed to update personal info", exception))
        }
    }

    // Work Experience Operations
    override suspend fun addWorkExperience(resumeId: Long, experience: WorkExperience): Result<Long> {
        return try {
            validateWorkExperience(experience)?.let { error ->
                return Result.failure(ValidationException("Work experience validation failed", listOf(error)))
            }

            val experienceEntity = WorkExperienceEntity.fromModel(experience.copy(resumeId = resumeId))
            val experienceId = resumeDao.insertExperience(experienceEntity)

            // Update resume timestamp
            resumeDao.updateResumeTimestamp(resumeId)

            Result.success(experienceId)
        } catch (exception: Exception) {
            Result.failure(RepositoryException("Failed to add work experience", exception))
        }
    }

    override suspend fun updateWorkExperience(experience: WorkExperience): Result<Unit> {
        return try {
            validateWorkExperience(experience)?.let { error ->
                return Result.failure(ValidationException("Work experience validation failed", listOf(error)))
            }

            val experienceEntity = WorkExperienceEntity.fromModel(experience)
            resumeDao.updateExperience(experienceEntity)

            // Update resume timestamp
            resumeDao.updateResumeTimestamp(experience.resumeId)

            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(RepositoryException("Failed to update work experience", exception))
        }
    }

    override suspend fun deleteWorkExperience(experienceId: Long): Result<Unit> {
        return try {
            resumeDao.deleteExperienceById(experienceId)
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(RepositoryException("Failed to delete work experience", exception))
        }
    }

    // Education Operations
    override suspend fun addEducation(resumeId: Long, education: Education): Result<Long> {
        return try {
            validateEducation(education)?.let { error ->
                return Result.failure(ValidationException("Education validation failed", listOf(error)))
            }

            val educationEntity = EducationEntity.fromModel(education.copy(resumeId = resumeId))
            val educationId = resumeDao.insertEducation(educationEntity)

            // Update resume timestamp
            resumeDao.updateResumeTimestamp(resumeId)

            Result.success(educationId)
        } catch (exception: Exception) {
            Result.failure(RepositoryException("Failed to add education", exception))
        }
    }

    override suspend fun updateEducation(education: Education): Result<Unit> {
        return try {
            validateEducation(education)?.let { error ->
                return Result.failure(ValidationException("Education validation failed", listOf(error)))
            }

            val educationEntity = EducationEntity.fromModel(education)
            resumeDao.updateEducation(educationEntity)

            // Update resume timestamp
            resumeDao.updateResumeTimestamp(education.resumeId)

            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(RepositoryException("Failed to update education", exception))
        }
    }

    override suspend fun deleteEducation(educationId: Long): Result<Unit> {
        return try {
            resumeDao.deleteEducationById(educationId)
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(RepositoryException("Failed to delete education", exception))
        }
    }

    // Skill Operations
    override suspend fun addSkill(resumeId: Long, skill: Skill): Result<Long> {
        return try {
            if (skill.name.isBlank()) {
                return Result.failure(ValidationException("Skill validation failed", listOf("Skill name cannot be empty")))
            }

            val skillEntity = SkillEntity.fromModel(skill.copy(resumeId = resumeId))
            val skillId = resumeDao.insertSkill(skillEntity)

            // Update resume timestamp
            resumeDao.updateResumeTimestamp(resumeId)

            Result.success(skillId)
        } catch (exception: Exception) {
            Result.failure(RepositoryException("Failed to add skill", exception))
        }
    }

    override suspend fun updateSkill(skill: Skill): Result<Unit> {
        return try {
            if (skill.name.isBlank()) {
                return Result.failure(ValidationException("Skill validation failed", listOf("Skill name cannot be empty")))
            }

            val skillEntity = SkillEntity.fromModel(skill)
            resumeDao.updateSkill(skillEntity)

            // Update resume timestamp
            resumeDao.updateResumeTimestamp(skill.resumeId)

            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(RepositoryException("Failed to update skill", exception))
        }
    }

    override suspend fun deleteSkill(skillId: Long): Result<Unit> {
        return try {
            resumeDao.deleteSkillById(skillId)
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(RepositoryException("Failed to delete skill", exception))
        }
    }

    // Project Operations
    override suspend fun addProject(resumeId: Long, project: Project): Result<Long> {
        return try {
            validateProject(project)?.let { error ->
                return Result.failure(ValidationException("Project validation failed", listOf(error)))
            }

            val projectEntity = ProjectEntity.fromModel(project.copy(resumeId = resumeId))
            val projectId = resumeDao.insertProject(projectEntity)

            // Update resume timestamp
            resumeDao.updateResumeTimestamp(resumeId)

            Result.success(projectId)
        } catch (exception: Exception) {
            Result.failure(RepositoryException("Failed to add project", exception))
        }
    }

    override suspend fun updateProject(project: Project): Result<Unit> {
        return try {
            validateProject(project)?.let { error ->
                return Result.failure(ValidationException("Project validation failed", listOf(error)))
            }

            val projectEntity = ProjectEntity.fromModel(project)
            resumeDao.updateProject(projectEntity)

            // Update resume timestamp
            resumeDao.updateResumeTimestamp(project.resumeId)

            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(RepositoryException("Failed to update project", exception))
        }
    }

    override suspend fun deleteProject(projectId: Long): Result<Unit> {
        return try {
            resumeDao.deleteProjectById(projectId)
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(RepositoryException("Failed to delete project", exception))
        }
    }

    // Analytics and Utility Operations
    override suspend fun getResumeCount(): Result<Int> {
        return try {
            val count = resumeDao.getResumeCount()
            Result.success(count)
        } catch (exception: Exception) {
            Result.failure(RepositoryException("Failed to get resume count", exception))
        }
    }

    override suspend fun getCompletedResumeCount(): Result<Int> {
        return try {
            val count = resumeDao.getCompletedResumeCount()
            Result.success(count)
        } catch (exception: Exception) {
            Result.failure(RepositoryException("Failed to get completed resume count", exception))
        }
    }

    override suspend fun getUsedTemplates(): Result<List<String>> {
        return try {
            val templates = resumeDao.getUsedTemplates()
            Result.success(templates)
        } catch (exception: Exception) {
            Result.failure(RepositoryException("Failed to get used templates", exception))
        }
    }

    // Additional utility methods for better UX
    suspend fun duplicateResume(resumeId: Long, newTitle: String): Result<Long> {
        return try {
            val originalResume = resumeDao.getCompleteResumeById(resumeId)
                ?: return Result.failure(RepositoryException("Resume not found", null))

            val duplicatedResume = originalResume.toModel().copy(
                id = 0L, // Reset ID for new resume
                title = newTitle,
                createdAt = Date(),
                updatedAt = Date(),
                isComplete = false
            )

            val newResumeId = resumeDao.insertCompleteResume(duplicatedResume)
            Result.success(newResumeId)
        } catch (exception: Exception) {
            Result.failure(RepositoryException("Failed to duplicate resume", exception))
        }
    }

    suspend fun getResumesByTemplate(templateId: String): Result<List<ResumeData>> {
        return try {
            val allResumes = resumeDao.getAllCompleteResumes()
            val filteredResumes = mutableListOf<ResumeData>()

            allResumes.collect { completeResumes ->
                completeResumes.filter { it.resume.templateId == templateId }
                    .forEach { filteredResumes.add(it.toModel()) }
            }

            Result.success(filteredResumes)
        } catch (exception: Exception) {
            Result.failure(RepositoryException("Failed to get resumes by template", exception))
        }
    }

    suspend fun getRecentlyUpdatedResumes(days: Int = 7): Result<List<ResumeData>> {
        return try {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_MONTH, -days)
            val sinceDate = calendar.time

            val entities = resumeDao.getRecentlyUpdatedResumes(sinceDate)
            val resumeData = mutableListOf<ResumeData>()

            entities.forEach { entity ->
                val completeResume = resumeDao.getCompleteResumeById(entity.id)
                completeResume?.let { resumeData.add(it.toModel()) }
            }

            Result.success(resumeData)
        } catch (exception: Exception) {
            Result.failure(RepositoryException("Failed to get recently updated resumes", exception))
        }
    }

    // Private validation methods
    private fun validateWorkExperience(experience: WorkExperience): String? {
        return when {
            experience.jobTitle.isBlank() -> "Job title is required"
            experience.company.isBlank() -> "Company name is required"
            experience.startDate != null && experience.endDate != null &&
                    !experience.isCurrentPosition && experience.startDate.after(experience.endDate) ->
                "Start date cannot be after end date"
            else -> null
        }
    }

    private fun validateEducation(education: Education): String? {
        return when {
            education.degree.isBlank() -> "Degree is required"
            education.institution.isBlank() -> "Institution name is required"
            else -> null
        }
    }

    private fun validateProject(project: Project): String? {
        return when {
            project.name.isBlank() -> "Project name is required"
            project.description.isBlank() -> "Project description is required"
            project.startDate != null && project.endDate != null &&
                    !project.isOngoing && project.startDate.after(project.endDate) ->
                "Start date cannot be after end date"
            else -> null
        }
    }
}

// Custom Exception Classes for better error handling
class RepositoryException(message: String, cause: Throwable?) : Exception(message, cause)
class ValidationException(message: String, val validationErrors: List<String>) : Exception(message)

// Extension functions for better error handling
fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (isSuccess) action(getOrNull()!!)
    return this
}

fun <T> Result<T>.onFailure(action: (Throwable) -> Unit): Result<T> {
    if (isFailure) exceptionOrNull()?.let(action)
    return this
}

// Data class for repository operations results
data class RepositoryResult<T>(
    val data: T? = null,
    val error: String? = null,
    val isLoading: Boolean = false
) {
    val isSuccess: Boolean get() = error == null && !isLoading
    val isError: Boolean get() = error != null

    companion object {
        fun <T> loading(): RepositoryResult<T> = RepositoryResult(isLoading = true)
        fun <T> success(data: T): RepositoryResult<T> = RepositoryResult(data = data)
        fun <T> error(error: String): RepositoryResult<T> = RepositoryResult(error = error)
    }
}