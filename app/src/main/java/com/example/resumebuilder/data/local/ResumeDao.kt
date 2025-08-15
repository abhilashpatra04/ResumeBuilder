// File: app/src/main/java/com/example/resumebuilder/data/local/ResumeDao.kt
package com.example.resumebuilder.data.local

import androidx.room.*
import com.example.resumebuilder.data.local.entities.*
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface ResumeDao {

    // Resume CRUD Operations
    @Query("SELECT * FROM resumes ORDER BY updatedAt DESC")
    fun getAllResumes(): Flow<List<ResumeEntity>>

    @Query("SELECT * FROM resumes WHERE id = :resumeId")
    suspend fun getResumeById(resumeId: Long): ResumeEntity?

    @Transaction
    @Query("SELECT * FROM resumes WHERE id = :resumeId")
    suspend fun getCompleteResumeById(resumeId: Long): CompleteResumeEntity?

    @Transaction
    @Query("SELECT * FROM resumes ORDER BY updatedAt DESC")
    fun getAllCompleteResumes(): Flow<List<CompleteResumeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResume(resume: ResumeEntity): Long

    @Update
    suspend fun updateResume(resume: ResumeEntity)

    @Delete
    suspend fun deleteResume(resume: ResumeEntity)

    @Query("DELETE FROM resumes WHERE id = :resumeId")
    suspend fun deleteResumeById(resumeId: Long)

    @Query("UPDATE resumes SET updatedAt = :updatedAt WHERE id = :resumeId")
    suspend fun updateResumeTimestamp(resumeId: Long, updatedAt: Date = Date())

    // Work Experience CRUD
    @Query("SELECT * FROM work_experiences WHERE resumeId = :resumeId ORDER BY sortOrder ASC")
    suspend fun getExperiencesByResumeId(resumeId: Long): List<WorkExperienceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExperience(experience: WorkExperienceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExperiences(experiences: List<WorkExperienceEntity>)

    @Update
    suspend fun updateExperience(experience: WorkExperienceEntity)

    @Delete
    suspend fun deleteExperience(experience: WorkExperienceEntity)

    @Query("DELETE FROM work_experiences WHERE id = :experienceId")
    suspend fun deleteExperienceById(experienceId: Long)

    @Query("DELETE FROM work_experiences WHERE resumeId = :resumeId")
    suspend fun deleteExperiencesByResumeId(resumeId: Long)

    // Education CRUD
    @Query("SELECT * FROM education WHERE resumeId = :resumeId ORDER BY sortOrder ASC")
    suspend fun getEducationByResumeId(resumeId: Long): List<EducationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEducation(education: EducationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEducationList(educationList: List<EducationEntity>)

    @Update
    suspend fun updateEducation(education: EducationEntity)

    @Delete
    suspend fun deleteEducation(education: EducationEntity)

    @Query("DELETE FROM education WHERE id = :educationId")
    suspend fun deleteEducationById(educationId: Long)

    @Query("DELETE FROM education WHERE resumeId = :resumeId")
    suspend fun deleteEducationByResumeId(resumeId: Long)

    // Skills CRUD
    @Query("SELECT * FROM skills WHERE resumeId = :resumeId ORDER BY sortOrder ASC")
    suspend fun getSkillsByResumeId(resumeId: Long): List<SkillEntity>

    @Query("SELECT * FROM skills WHERE resumeId = :resumeId AND category = :category ORDER BY sortOrder ASC")
    suspend fun getSkillsByCategory(resumeId: Long, category: String): List<SkillEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkill(skill: SkillEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkills(skills: List<SkillEntity>)

    @Update
    suspend fun updateSkill(skill: SkillEntity)

    @Delete
    suspend fun deleteSkill(skill: SkillEntity)

    @Query("DELETE FROM skills WHERE id = :skillId")
    suspend fun deleteSkillById(skillId: Long)

    @Query("DELETE FROM skills WHERE resumeId = :resumeId")
    suspend fun deleteSkillsByResumeId(resumeId: Long)

    // Projects CRUD
    @Query("SELECT * FROM projects WHERE resumeId = :resumeId ORDER BY sortOrder ASC")
    suspend fun getProjectsByResumeId(resumeId: Long): List<ProjectEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjects(projects: List<ProjectEntity>)

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Delete
    suspend fun deleteProject(project: ProjectEntity)

    @Query("DELETE FROM projects WHERE id = :projectId")
    suspend fun deleteProjectById(projectId: Long)

    @Query("DELETE FROM projects WHERE resumeId = :resumeId")
    suspend fun deleteProjectsByResumeId(resumeId: Long)

    // Certifications CRUD
    @Query("SELECT * FROM certifications WHERE resumeId = :resumeId ORDER BY sortOrder ASC")
    suspend fun getCertificationsByResumeId(resumeId: Long): List<CertificationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCertification(certification: CertificationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCertifications(certifications: List<CertificationEntity>)

    @Update
    suspend fun updateCertification(certification: CertificationEntity)

    @Delete
    suspend fun deleteCertification(certification: CertificationEntity)

    @Query("DELETE FROM certifications WHERE id = :certificationId")
    suspend fun deleteCertificationById(certificationId: Long)

    @Query("DELETE FROM certifications WHERE resumeId = :resumeId")
    suspend fun deleteCertificationsByResumeId(resumeId: Long)

    // Languages CRUD
    @Query("SELECT * FROM languages WHERE resumeId = :resumeId ORDER BY sortOrder ASC")
    suspend fun getLanguagesByResumeId(resumeId: Long): List<LanguageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLanguage(language: LanguageEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLanguages(languages: List<LanguageEntity>)

    @Update
    suspend fun updateLanguage(language: LanguageEntity)

    @Delete
    suspend fun deleteLanguage(language: LanguageEntity)

    @Query("DELETE FROM languages WHERE id = :languageId")
    suspend fun deleteLanguageById(languageId: Long)

    @Query("DELETE FROM languages WHERE resumeId = :resumeId")
    suspend fun deleteLanguagesByResumeId(resumeId: Long)

    // References CRUD
    @Query("SELECT * FROM references WHERE resumeId = :resumeId ORDER BY sortOrder ASC")
    suspend fun getReferencesByResumeId(resumeId: Long): List<ReferenceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReference(reference: ReferenceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReferences(references: List<ReferenceEntity>)

    @Update
    suspend fun updateReference(reference: ReferenceEntity)

    @Delete
    suspend fun deleteReference(reference: ReferenceEntity)

    @Query("DELETE FROM references WHERE id = :referenceId")
    suspend fun deleteReferenceById(referenceId: Long)

    @Query("DELETE FROM references WHERE resumeId = :resumeId")
    suspend fun deleteReferencesByResumeId(resumeId: Long)

    // Custom Sections CRUD
    @Query("SELECT * FROM custom_sections WHERE resumeId = :resumeId ORDER BY sortOrder ASC")
    suspend fun getCustomSectionsByResumeId(resumeId: Long): List<CustomSectionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomSection(customSection: CustomSectionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomSections(customSections: List<CustomSectionEntity>)

    @Update
    suspend fun updateCustomSection(customSection: CustomSectionEntity)

    @Delete
    suspend fun deleteCustomSection(customSection: CustomSectionEntity)

    @Query("DELETE FROM custom_sections WHERE id = :customSectionId")
    suspend fun deleteCustomSectionById(customSectionId: Long)

    @Query("DELETE FROM custom_sections WHERE resumeId = :resumeId")
    suspend fun deleteCustomSectionsByResumeId(resumeId: Long)

    // Transaction Operations for Complete Resume Operations
    @Transaction
    suspend fun insertCompleteResume(resumeData: com.example.resumebuilder.model.ResumeData): Long {
        val resumeEntity = ResumeEntity(
            id = resumeData.id,
            title = resumeData.title,
            templateId = resumeData.templateId,
            personalInfo = PersonalInfoEmbedded.fromModel(resumeData.personalInfo),
            createdAt = resumeData.createdAt,
            updatedAt = resumeData.updatedAt,
            isComplete = resumeData.isComplete
        )

        val resumeId = insertResume(resumeEntity)

        // Insert all related data with the new resume ID
        if (resumeData.experiences.isNotEmpty()) {
            val experiences = resumeData.experiences.map {
                WorkExperienceEntity.fromModel(it.copy(resumeId = resumeId))
            }
            insertExperiences(experiences)
        }

        if (resumeData.education.isNotEmpty()) {
            val education = resumeData.education.map {
                EducationEntity.fromModel(it.copy(resumeId = resumeId))
            }
            insertEducationList(education)
        }

        if (resumeData.skills.isNotEmpty()) {
            val skills = resumeData.skills.map {
                SkillEntity.fromModel(it.copy(resumeId = resumeId))
            }
            insertSkills(skills)
        }

        if (resumeData.projects.isNotEmpty()) {
            val projects = resumeData.projects.map {
                ProjectEntity.fromModel(it.copy(resumeId = resumeId))
            }
            insertProjects(projects)
        }

        if (resumeData.certifications.isNotEmpty()) {
            val certifications = resumeData.certifications.map {
                CertificationEntity.fromModel(it.copy(resumeId = resumeId))
            }
            insertCertifications(certifications)
        }

        if (resumeData.languages.isNotEmpty()) {
            val languages = resumeData.languages.map {
                LanguageEntity.fromModel(it.copy(resumeId = resumeId))
            }
            insertLanguages(languages)
        }

        if (resumeData.references.isNotEmpty()) {
            val references = resumeData.references.map {
                ReferenceEntity.fromModel(it.copy(resumeId = resumeId))
            }
            insertReferences(references)
        }

        if (resumeData.customSections.isNotEmpty()) {
            val customSections = resumeData.customSections.map {
                CustomSectionEntity.fromModel(it.copy(resumeId = resumeId))
            }
            insertCustomSections(customSections)
        }

        return resumeId
    }

    @Transaction
    suspend fun updateCompleteResume(resumeData: com.example.resumebuilder.model.ResumeData) {
        val resumeEntity = ResumeEntity(
            id = resumeData.id,
            title = resumeData.title,
            templateId = resumeData.templateId,
            personalInfo = PersonalInfoEmbedded.fromModel(resumeData.personalInfo),
            createdAt = resumeData.createdAt,
            updatedAt = Date(), // Update timestamp
            isComplete = resumeData.isComplete
        )

        updateResume(resumeEntity)

        // Delete existing related data and insert new data
        val resumeId = resumeData.id

        deleteExperiencesByResumeId(resumeId)
        deleteEducationByResumeId(resumeId)
        deleteSkillsByResumeId(resumeId)
        deleteProjectsByResumeId(resumeId)
        deleteCertificationsByResumeId(resumeId)
        deleteLanguagesByResumeId(resumeId)
        deleteReferencesByResumeId(resumeId)
        deleteCustomSectionsByResumeId(resumeId)

        // Insert updated data
        if (resumeData.experiences.isNotEmpty()) {
            val experiences = resumeData.experiences.map {
                WorkExperienceEntity.fromModel(it.copy(resumeId = resumeId))
            }
            insertExperiences(experiences)
        }

        if (resumeData.education.isNotEmpty()) {
            val education = resumeData.education.map {
                EducationEntity.fromModel(it.copy(resumeId = resumeId))
            }
            insertEducationList(education)
        }

        if (resumeData.skills.isNotEmpty()) {
            val skills = resumeData.skills.map {
                SkillEntity.fromModel(it.copy(resumeId = resumeId))
            }
            insertSkills(skills)
        }

        if (resumeData.projects.isNotEmpty()) {
            val projects = resumeData.projects.map {
                ProjectEntity.fromModel(it.copy(resumeId = resumeId))
            }
            insertProjects(projects)
        }

        if (resumeData.certifications.isNotEmpty()) {
            val certifications = resumeData.certifications.map {
                CertificationEntity.fromModel(it.copy(resumeId = resumeId))
            }
            insertCertifications(certifications)
        }

        if (resumeData.languages.isNotEmpty()) {
            val languages = resumeData.languages.map {
                LanguageEntity.fromModel(it.copy(resumeId = resumeId))
            }
            insertLanguages(languages)
        }

        if (resumeData.references.isNotEmpty()) {
            val references = resumeData.references.map {
                ReferenceEntity.fromModel(it.copy(resumeId = resumeId))
            }
            insertReferences(references)
        }

        if (resumeData.customSections.isNotEmpty()) {
            val customSections = resumeData.customSections.map {
                CustomSectionEntity.fromModel(it.copy(resumeId = resumeId))
            }
            insertCustomSections(customSections)
        }
    }

    @Transaction
    suspend fun deleteCompleteResume(resumeId: Long) {
        deleteExperiencesByResumeId(resumeId)
        deleteEducationByResumeId(resumeId)
        deleteSkillsByResumeId(resumeId)
        deleteProjectsByResumeId(resumeId)
        deleteCertificationsByResumeId(resumeId)
        deleteLanguagesByResumeId(resumeId)
        deleteReferencesByResumeId(resumeId)
        deleteCustomSectionsByResumeId(resumeId)
        deleteResumeById(resumeId)
    }

    // Utility queries for analytics and search
    @Query("SELECT COUNT(*) FROM resumes")
    suspend fun getResumeCount(): Int

    @Query("SELECT COUNT(*) FROM resumes WHERE isComplete = 1")
    suspend fun getCompletedResumeCount(): Int

    @Query("SELECT * FROM resumes WHERE title LIKE '%' || :searchQuery || '%' OR personalInfo.fullName LIKE '%' || :searchQuery || '%'")
    suspend fun searchResumes(searchQuery: String): List<ResumeEntity>

    @Query("SELECT DISTINCT templateId FROM resumes")
    suspend fun getUsedTemplates(): List<String>

    @Query("SELECT * FROM resumes WHERE updatedAt >= :sinceDate ORDER BY updatedAt DESC")
    suspend fun getRecentlyUpdatedResumes(sinceDate: Date): List<ResumeEntity>
}