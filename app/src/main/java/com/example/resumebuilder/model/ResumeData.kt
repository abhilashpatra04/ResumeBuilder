package com.example.resumebuilder.model

import java.text.SimpleDateFormat
import java.util.*

// Main Resume Data Container
data class ResumeData(
    val id: Long = 0L,
    val title: String = "",
    val templateId: String = "olivia",
    val personalInfo: PersonalInfo = PersonalInfo(),
    val experiences: List<WorkExperience> = emptyList(),
    val education: List<Education> = emptyList(),
    val skills: List<Skill> = emptyList(),
    val projects: List<Project> = emptyList(),
    val certifications: List<Certification> = emptyList(),
    val languages: List<Language> = emptyList(),
    val references: List<Reference> = emptyList(),
    val customSections: List<CustomSection> = emptyList(),
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val isComplete: Boolean = false
) {
    fun validate(): ValidationResult {
        val errors = mutableListOf<String>()

        if (personalInfo.fullName.isBlank()) errors.add("Full name is required")
        if (personalInfo.email.isBlank()) errors.add("Email is required")
        if (!personalInfo.email.contains("@")) errors.add("Valid email is required")
        if (personalInfo.jobTitle.isBlank()) errors.add("Job title is required")

        return ValidationResult(errors.isEmpty(), errors)
    }
}

// Personal Information
data class PersonalInfo(
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
    fun getFormattedAddress(): String {
        return listOfNotNull(
            address.takeIf { it.isNotBlank() },
            city.takeIf { it.isNotBlank() },
            state.takeIf { it.isNotBlank() },
            zipCode.takeIf { it.isNotBlank() },
            country.takeIf { it.isNotBlank() }
        ).joinToString(", ")
    }
}

// Work Experience
data class WorkExperience(
    val id: Long = 0L,
    val resumeId: Long = 0L,
    val jobTitle: String = "",
    val company: String = "",
    val location: String = "",
    val startDate: Date? = null,
    val endDate: Date? = null,
    val isCurrentPosition: Boolean = false,
    val description: String = "",
    val bulletPoints: List<String> = emptyList(),
    val achievements: List<String> = emptyList(),
    val technologies: List<String> = emptyList(),
    val sortOrder: Int = 0
) {
    fun getFormattedDuration(): String {
        val dateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
        val start = startDate?.let { dateFormat.format(it) } ?: "Present"
        val end = if (isCurrentPosition) "Present" else endDate?.let { dateFormat.format(it) } ?: "Present"
        return "$start - $end"
    }

    fun getDurationInMonths(): Int {
        val start = startDate ?: return 0
        val end = if (isCurrentPosition) Date() else (endDate ?: Date())

        val calendar1 = Calendar.getInstance().apply { time = start }
        val calendar2 = Calendar.getInstance().apply { time = end }

        val yearDiff = calendar2.get(Calendar.YEAR) - calendar1.get(Calendar.YEAR)
        val monthDiff = calendar2.get(Calendar.MONTH) - calendar1.get(Calendar.MONTH)

        return yearDiff * 12 + monthDiff
    }
}

// Education
data class Education(
    val id: Long = 0L,
    val resumeId: Long = 0L,
    val degree: String = "",
    val fieldOfStudy: String = "",
    val institution: String = "",
    val location: String = "",
    val graduationDate: Date? = null,
    val gpa: String = "",
    val maxGpa: String = "4.0",
    val relevantCourses: List<String> = emptyList(),
    val honors: List<String> = emptyList(),
    val activities: List<String> = emptyList(),
    val thesis: String = "",
    val sortOrder: Int = 0
) {
    fun getFormattedGraduation(): String {
        return graduationDate?.let {
            SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(it)
        } ?: ""
    }

    fun getFormattedGpa(): String {
        return if (gpa.isNotBlank() && maxGpa.isNotBlank()) {
            "GPA: $gpa/$maxGpa"
        } else ""
    }
}

// Skills
data class Skill(
    val id: Long = 0L,
    val resumeId: Long = 0L,
    val name: String = "",
    val category: SkillCategory = SkillCategory.TECHNICAL,
    val proficiencyLevel: ProficiencyLevel = ProficiencyLevel.INTERMEDIATE,
    val yearsOfExperience: Int = 0,
    val keywords: List<String> = emptyList(),
    val sortOrder: Int = 0
)

// Projects
data class Project(
    val id: Long = 0L,
    val resumeId: Long = 0L,
    val name: String = "",
    val description: String = "",
    val role: String = "",
    val startDate: Date? = null,
    val endDate: Date? = null,
    val isOngoing: Boolean = false,
    val technologies: List<String> = emptyList(),
    val features: List<String> = emptyList(),
    val challenges: String = "",
    val solutions: String = "",
    val results: String = "",
    val projectUrl: String = "",
    val githubUrl: String = "",
    val demoUrl: String = "",
    val images: List<String> = emptyList(),
    val sortOrder: Int = 0
) {
    fun getFormattedDuration(): String {
        val dateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
        val start = startDate?.let { dateFormat.format(it) } ?: ""
        val end = if (isOngoing) "Ongoing" else endDate?.let { dateFormat.format(it) } ?: ""

        return when {
            start.isBlank() && end.isBlank() -> ""
            start.isBlank() -> end
            end.isBlank() -> start
            else -> "$start - $end"
        }
    }
}

// Certifications
data class Certification(
    val id: Long = 0L,
    val resumeId: Long = 0L,
    val name: String = "",
    val issuingOrganization: String = "",
    val issueDate: Date? = null,
    val expirationDate: Date? = null,
    val credentialId: String = "",
    val credentialUrl: String = "",
    val description: String = "",
    val skills: List<String> = emptyList(),
    val sortOrder: Int = 0
) {
    fun isExpired(): Boolean {
        return expirationDate?.before(Date()) ?: false
    }

    fun getFormattedIssueDate(): String {
        return issueDate?.let {
            SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(it)
        } ?: ""
    }
}

// Languages
data class Language(
    val id: Long = 0L,
    val resumeId: Long = 0L,
    val name: String = "",
    val proficiency: LanguageProficiency = LanguageProficiency.INTERMEDIATE,
    val nativeLanguage: Boolean = false,
    val certifications: List<String> = emptyList(),
    val sortOrder: Int = 0
)

// References
data class Reference(
    val id: Long = 0L,
    val resumeId: Long = 0L,
    val name: String = "",
    val title: String = "",
    val company: String = "",
    val email: String = "",
    val phone: String = "",
    val relationship: String = "",
    val yearsKnown: Int = 0,
    val notes: String = "",
    val sortOrder: Int = 0
)

// Custom Sections for flexibility
data class CustomSection(
    val id: Long = 0L,
    val resumeId: Long = 0L,
    val title: String = "",
    val content: String = "",
    val items: List<CustomSectionItem> = emptyList(),
    val sectionType: CustomSectionType = CustomSectionType.TEXT,
    val sortOrder: Int = 0
)

data class CustomSectionItem(
    val id: Long = 0L,
    val title: String = "",
    val subtitle: String = "",
    val description: String = "",
    val date: Date? = null,
    val url: String = "",
    val sortOrder: Int = 0
)

// Enums for standardized values
enum class SkillCategory(val displayName: String) {
    TECHNICAL("Technical Skills"),
    SOFTWARE("Software"),
    PROGRAMMING("Programming Languages"),
    FRAMEWORKS("Frameworks & Libraries"),
    DATABASES("Databases"),
    CLOUD("Cloud Technologies"),
    TOOLS("Tools & Technologies"),
    SOFT_SKILLS("Soft Skills"),
    LANGUAGES("Languages"),
    CERTIFICATIONS("Certifications"),
    OTHER("Other")
}

enum class ProficiencyLevel(val displayName: String, val value: Int) {
    BEGINNER("Beginner", 1),
    BASIC("Basic", 2),
    INTERMEDIATE("Intermediate", 3),
    ADVANCED("Advanced", 4),
    EXPERT("Expert", 5)
}

enum class LanguageProficiency(val displayName: String, val description: String) {
    NATIVE("Native", "Native or bilingual proficiency"),
    FLUENT("Fluent", "Full professional proficiency"),
    ADVANCED("Advanced", "Professional working proficiency"),
    INTERMEDIATE("Intermediate", "Limited working proficiency"),
    BASIC("Basic", "Elementary proficiency"),
    BEGINNER("Beginner", "Basic conversational skills")
}

enum class CustomSectionType(val displayName: String) {
    TEXT("Plain Text"),
    LIST("Bulleted List"),
    TIMELINE("Timeline/Dates"),
    LINKS("Links/URLs"),
    ACHIEVEMENTS("Achievements"),
    PUBLICATIONS("Publications"),
    VOLUNTEER("Volunteer Work"),
    AWARDS("Awards & Recognition"),
    INTERESTS("Interests & Hobbies")
}

// Validation Result
data class ValidationResult(
    val isValid: Boolean,
    val errors: List<String> = emptyList()
)

// Template Configuration
data class TemplateConfig(
    val id: String,
    val name: String,
    val description: String,
    val category: TemplateCategory,
    val colorScheme: ColorScheme,
    val layout: LayoutType,
    val sectionsOrder: List<SectionType>,
    val customizable: Boolean = true,
    val isPremium: Boolean = false
)

enum class TemplateCategory(val displayName: String) {
    PROFESSIONAL("Professional"),
    CREATIVE("Creative"),
    MODERN("Modern"),
    CLASSIC("Classic"),
    TECHNICAL("Technical"),
    ACADEMIC("Academic"),
    EXECUTIVE("Executive")
}

enum class ColorScheme(val displayName: String) {
    BLUE("Professional Blue"),
    GREEN("Nature Green"),
    RED("Corporate Red"),
    PURPLE("Creative Purple"),
    ORANGE("Energetic Orange"),
    BLACK("Classic Black"),
    CUSTOM("Custom Colors")
}

enum class LayoutType(val displayName: String) {
    SINGLE_COLUMN("Single Column"),
    TWO_COLUMN("Two Column"),
    SIDEBAR("Sidebar Layout"),
    CREATIVE("Creative Layout")
}

enum class SectionType(val displayName: String) {
    PERSONAL_INFO("Personal Information"),
    SUMMARY("Professional Summary"),
    EXPERIENCE("Work Experience"),
    EDUCATION("Education"),
    SKILLS("Skills"),
    PROJECTS("Projects"),
    CERTIFICATIONS("Certifications"),
    LANGUAGES("Languages"),
    REFERENCES("References"),
    CUSTOM("Custom Section")
}