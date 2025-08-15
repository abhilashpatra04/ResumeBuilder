package com.example.resumebuilder.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.resumebuilder.model.Reference


// File: app/src/main/java/com/example/resumebuilder/data/local/entities/ReferenceEntity.kt
// Add this to your existing ResumeEntity.kt file or create as separate file

@Entity(tableName = "reference_table") // Changed from "references" to avoid SQL keyword conflict
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