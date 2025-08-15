// File: app/src/main/java/com/example/resumebuilder/data/local/entities/ResumeEntity.kt
package com.example.resumebuilder.data.local.entities

import androidx.room.*
import com.example.resumebuilder.model.*
import java.util.*

// Main Resume Entity
@Entity(tableName = "resumes")
data class ResumeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val templateId: String,
    @Embedded
    val personalInfo: PersonalInfoEmbedded,
    val createdAt: Date,
    val updatedAt: Date,
    val isComplete: Boolean = false
)

@Entity(tableName = "work_experiences")
data class WorkExperienceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val resumeId: Long,
    val jobTitle: String,
    val company: String,
    val location: String,
    val startDate: Date?,
    val endDate: Date?,
    val isCurrentPosition: Boolean,
    val description: String,
    val bulletPoints: List<String>,
    val achievements: List<String>,
    val technologies: List<String>,
    val sortOrder: Int
) {
    companion object {
        fun fromModel(experience: WorkExperience): WorkExperienceEntity {
            return WorkExperienceEntity(
                id = experience.id,
                resumeId = experience.resumeId,
                jobTitle = experience.jobTitle,
                company = experience.company,
                location = experience.location,
                startDate = experience.startDate,
                endDate = experience.endDate,
                isCurrentPosition = experience.isCurrentPosition,
                description = experience.description,
                bulletPoints = experience.bulletPoints,
                achievements = experience.achievements,
                technologies = experience.technologies,
                sortOrder = experience.sortOrder
            )
        }
    }

    fun toModel(): WorkExperience {
        return WorkExperience(
            id = id,
            resumeId = resumeId,
            jobTitle = jobTitle,
            company = company,
            location = location,
            startDate = startDate,
            endDate = endDate,
            isCurrentPosition = isCurrentPosition,
            description = description,
            bulletPoints = bulletPoints,
            achievements = achievements,
            technologies = technologies,
            sortOrder = sortOrder
        )
    }
}

@Entity(tableName = "education")
data class EducationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val resumeId: Long,
    val degree: String,
    val fieldOfStudy: String,
    val institution: String,
    val location: String,
    val graduationDate: Date?,
    val gpa: String,
    val maxGpa: String,
    val relevantCourses: List<String>,
    val honors: List<String>,
    val activities: List<String>,
    val thesis: String,
    val sortOrder: Int
) {
    companion object {
        fun fromModel(education: Education): EducationEntity {
            return EducationEntity(
                id = education.id,
                resumeId = education.resumeId,
                degree = education.degree,
                fieldOfStudy = education.fieldOfStudy,
                institution = education.institution,
                location = education.location,
                graduationDate = education.graduationDate,
                gpa = education.gpa,
                maxGpa = education.maxGpa,
                relevantCourses = education.relevantCourses,
                honors = education.honors,
                activities = education.activities,
                thesis = education.thesis,
                sortOrder = education.sortOrder
            )
        }
    }

    fun toModel(): Education {
        return Education(
            id = id,
            resumeId = resumeId,
            degree = degree,
            fieldOfStudy = fieldOfStudy,
            institution = institution,
            location = location,
            graduationDate = graduationDate,
            gpa = gpa,
            maxGpa = maxGpa,
            relevantCourses = relevantCourses,
            honors = honors,
            activities = activities,
            thesis = thesis,
            sortOrder = sortOrder
        )
    }
}

@Entity(tableName = "skills")
data class SkillEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val resumeId: Long,
    val name: String,
    val category: SkillCategory,
    val proficiencyLevel: ProficiencyLevel,
    val yearsOfExperience: Int,
    val keywords: List<String>,
    val sortOrder: Int
) {
    companion object {
        fun fromModel(skill: Skill): SkillEntity {
            return SkillEntity(
                id = skill.id,
                resumeId = skill.resumeId,
                name = skill.name,
                category = skill.category,
                proficiencyLevel = skill.proficiencyLevel,
                yearsOfExperience = skill.yearsOfExperience,
                keywords = skill.keywords,
                sortOrder = skill.sortOrder
            )
        }
    }

    fun toModel(): Skill {
        return Skill(
            id = id,
            resumeId = resumeId,
            name = name,
            category = category,
            proficiencyLevel = proficiencyLevel,
            yearsOfExperience = yearsOfExperience,
            keywords = keywords,
            sortOrder = sortOrder
        )
    }
}

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val resumeId: Long,
    val name: String,
    val description: String,
    val role: String,
    val startDate: Date?,
    val endDate: Date?,
    val isOngoing: Boolean,
    val technologies: List<String>,
    val features: List<String>,
    val challenges: String,
    val solutions: String,
    val results: String,
    val projectUrl: String,
    val githubUrl: String,
    val demoUrl: String,
    val images: List<String>,
    val sortOrder: Int
) {
    companion object {
        fun fromModel(project: Project): ProjectEntity {
            return ProjectEntity(
                id = project.id,
                resumeId = project.resumeId,
                name = project.name,
                description = project.description,
                role = project.role,
                startDate = project.startDate,
                endDate = project.endDate,
                isOngoing = project.isOngoing,
                technologies = project.technologies,
                features = project.features,
                challenges = project.challenges,
                solutions = project.solutions,
                results = project.results,
                projectUrl = project.projectUrl,
                githubUrl = project.githubUrl,
                demoUrl = project.demoUrl,
                images = project.images,
                sortOrder = project.sortOrder
            )
        }
    }

    fun toModel(): Project {
        return Project(
            id = id,
            resumeId = resumeId,
            name = name,
            description = description,
            role = role,
            startDate = startDate,
            endDate = endDate,
            isOngoing = isOngoing,
            technologies = technologies,
            features = features,
            challenges = challenges,
            solutions = solutions,
            results = results,
            projectUrl = projectUrl,
            githubUrl = githubUrl,
            demoUrl = demoUrl,
            images = images,
            sortOrder = sortOrder
        )
    }
}

@Entity(tableName = "certifications")
data class CertificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val resumeId: Long,
    val name: String,
    val issuingOrganization: String,
    val issueDate: Date?,
    val expirationDate: Date?,
    val credentialId: String,
    val credentialUrl: String,
    val description: String,
    val skills: List<String>,
    val sortOrder: Int
) {
    companion object {
        fun fromModel(certification: Certification): CertificationEntity {
            return CertificationEntity(
                id = certification.id,
                resumeId = certification.resumeId,
                name = certification.name,
                issuingOrganization = certification.issuingOrganization,
                issueDate = certification.issueDate,
                expirationDate = certification.expirationDate,
                credentialId = certification.credentialId,
                credentialUrl = certification.credentialUrl,
                description = certification.description,
                skills = certification.skills,
                sortOrder = certification.sortOrder
            )
        }
    }

    fun toModel(): Certification {
        return Certification(
            id = id,
            resumeId = resumeId,
            name = name,
            issuingOrganization = issuingOrganization,
            issueDate = issueDate,
            expirationDate = expirationDate,
            credentialId = credentialId,
            credentialUrl = credentialUrl,
            description = description,
            skills = skills,
            sortOrder = sortOrder
        )
    }
}

@Entity(tableName = "languages")
data class LanguageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val resumeId: Long,
    val name: String,
    val proficiency: LanguageProficiency,
    val nativeLanguage: Boolean,
    val certifications: List<String>,
    val sortOrder: Int
) {
    companion object {
        fun fromModel(language: Language): LanguageEntity {
            return LanguageEntity(
                id = language.id,
                resumeId = language.resumeId,
                name = language.name,
                proficiency = language.proficiency,
                nativeLanguage = language.nativeLanguage,
                certifications = language.certifications,
                sortOrder = language.sortOrder
            )
        }
    }

    fun toModel(): Language {
        return Language(
            id = id,
            resumeId = resumeId,
            name = name,
            proficiency = proficiency,
            nativeLanguage = nativeLanguage,
            certifications = certifications,
            sortOrder = sortOrder
        )
    }
}

@Entity(tableName = "references")
data class ReferenceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val resumeId: Long,
    val name: String,
    val title: String,
    val company: String,
    val email: String,
    val phone: String,
    val relationship: String,
    val yearsKnown: Int,
    val notes: String,
    val sortOrder: Int
) {
    companion object {
        fun fromModel(reference: Reference): ReferenceEntity {
            return ReferenceEntity(
                id = reference.id,
                resumeId = reference.resumeId,
                name = reference.name,
                title = reference.title,
                company = reference.company,
                email = reference.email,
                phone = reference.phone,
                relationship = reference.relationship,
                yearsKnown = reference.yearsKnown,
                notes = reference.notes,
                sortOrder = reference.sortOrder
            )
        }
    }

    fun toModel(): Reference {
        return Reference(
            id = id,
            resumeId = resumeId,
            name = name,
            title = title,
            company = company,
            email = email,
            phone = phone,
            relationship = relationship,
            yearsKnown = yearsKnown,
            notes = notes,
            sortOrder = sortOrder
        )
    }
}

@Entity(tableName = "custom_sections")
data class CustomSectionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val resumeId: Long,
    val title: String,
    val content: String,
    val items: List<CustomSectionItem>,
    val sectionType: CustomSectionType,
    val sortOrder: Int
) {
    companion object {
        fun fromModel(customSection: CustomSection): CustomSectionEntity {
            return CustomSectionEntity(
                id = customSection.id,
                resumeId = customSection.resumeId,
                title = customSection.title,
                content = customSection.content,
                items = customSection.items,
                sectionType = customSection.sectionType,
                sortOrder = customSection.sortOrder
            )
        }
    }

    fun toModel(): CustomSection {
        return CustomSection(
            id = id,
            resumeId = resumeId,
            title = title,
            content = content,
            items = items,
            sectionType = sectionType,
            sortOrder = sortOrder
        )
    }
}

// Embedded class for PersonalInfo to avoid separate table
data class PersonalInfoEmbedded(
    val fullName: String = "",
    val jobTitle: String = "",
    val email: String = "",
    val phone: String = "",
    val linkedIn: String = "",
    val github: String = "",
    val website: String = "",
    val address: String = "",
    val city: String = "",
    val state: String = "",
    val zipCode: String = "",
    val country: String = "",
    val profilePhotoPath: String = "",
    val summary: String = ""
) {
    companion object {
        fun fromModel(personalInfo: PersonalInfo): PersonalInfoEmbedded {
            return PersonalInfoEmbedded(
                fullName = personalInfo.fullName,
                jobTitle = personalInfo.jobTitle,
                email = personalInfo.email,
                phone = personalInfo.phone,
                linkedIn = personalInfo.linkedIn,
                github = personalInfo.github,
                website = personalInfo.website,
                address = personalInfo.address,
                city = personalInfo.city,
                state = personalInfo.state,
                zipCode = personalInfo.zipCode,
                country = personalInfo.country,
                profilePhotoPath = personalInfo.profilePhotoPath,
                summary = personalInfo.summary
            )
        }
    }

    fun toModel(): PersonalInfo {
        return PersonalInfo(
            fullName = fullName,
            jobTitle = jobTitle,
            email = email,
            phone = phone,
            linkedIn = linkedIn,
            github = github,
            website = website,
            address = address,
            city = city,
            state = state,
            zipCode = zipCode,
            country = country,
            profilePhotoPath = profilePhotoPath,
            summary = summary
        )
    }
}

// Data class for complete resume with all sections
data class CompleteResumeEntity(
    @Embedded val resume: ResumeEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "resumeId"
    )
    val experiences: List<WorkExperienceEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "resumeId"
    )
    val education: List<EducationEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "resumeId"
    )
    val skills: List<SkillEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "resumeId"
    )
    val projects: List<ProjectEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "resumeId"
    )
    val certifications: List<CertificationEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "resumeId"
    )


    val languages: List<LanguageEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "resumeId"
    )
    val references: List<ReferenceEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "resumeId"
    )
    val customSections: List<CustomSectionEntity>
) {
    fun toModel(): ResumeData {
        return ResumeData(
            id = resume.id,
            title = resume.title,
            templateId = resume.templateId,
            personalInfo = resume.personalInfo.toModel(),
            experiences = experiences.map { it.toModel() },
            education = education.map { it.toModel() },
            skills = skills.map { it.toModel() },
            projects = projects.map { it.toModel() },
            certifications = certifications.map { it.toModel() },
            languages = languages.map { it.toModel() },
            references = references.map { it.toModel() },
            customSections = customSections.map { it.toModel() },
            createdAt = resume.createdAt,
            updatedAt = resume.updatedAt,
            isComplete = resume.isComplete
        )
    }
}
