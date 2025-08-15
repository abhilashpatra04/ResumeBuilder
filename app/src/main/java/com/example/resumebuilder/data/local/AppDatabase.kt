// File: app/src/main/java/com/example/resumebuilder/data/local/AppDatabase.kt
package com.example.resumebuilder.data.local

import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.resumebuilder.data.local.entities.*
import com.example.resumebuilder.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

@Database(
    entities = [
        ResumeEntity::class,
        WorkExperienceEntity::class,
        EducationEntity::class,
        SkillEntity::class,
        ProjectEntity::class,
        CertificationEntity::class,
        LanguageEntity::class,
        ReferenceEntity::class,
        CustomSectionEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun resumeDao(): ResumeDao

    companion object {
        const val DATABASE_NAME = "resume_builder_database"
    }
}

// Type Converters for complex data types
class Converters {
    private val gson = Gson()

    // Date Converters
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    // String List Converters
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    // Skill Category Converter
    @TypeConverter
    fun fromSkillCategory(category: SkillCategory): String {
        return category.name
    }

    @TypeConverter
    fun toSkillCategory(categoryName: String): SkillCategory {
        return try {
            SkillCategory.valueOf(categoryName)
        } catch (e: IllegalArgumentException) {
            SkillCategory.TECHNICAL
        }
    }

    // Proficiency Level Converter
    @TypeConverter
    fun fromProficiencyLevel(level: ProficiencyLevel): String {
        return level.name
    }

    @TypeConverter
    fun toProficiencyLevel(levelName: String): ProficiencyLevel {
        return try {
            ProficiencyLevel.valueOf(levelName)
        } catch (e: IllegalArgumentException) {
            ProficiencyLevel.INTERMEDIATE
        }
    }

    // Language Proficiency Converter
    @TypeConverter
    fun fromLanguageProficiency(proficiency: LanguageProficiency): String {
        return proficiency.name
    }

    @TypeConverter
    fun toLanguageProficiency(proficiencyName: String): LanguageProficiency {
        return try {
            LanguageProficiency.valueOf(proficiencyName)
        } catch (e: IllegalArgumentException) {
            LanguageProficiency.INTERMEDIATE
        }
    }

    // Custom Section Type Converter
    @TypeConverter
    fun fromCustomSectionType(type: CustomSectionType): String {
        return type.name
    }

    @TypeConverter
    fun toCustomSectionType(typeName: String): CustomSectionType {
        return try {
            CustomSectionType.valueOf(typeName)
        } catch (e: IllegalArgumentException) {
            CustomSectionType.TEXT
        }
    }

    // Custom Section Item List Converter
    @TypeConverter
    fun fromCustomSectionItemList(items: List<CustomSectionItem>?): String {
        return gson.toJson(items)
    }

    @TypeConverter
    fun toCustomSectionItemList(value: String): List<CustomSectionItem> {
        val listType = object : TypeToken<List<CustomSectionItem>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }
}

// Database Migration Strategies
object DatabaseMigrations {

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Example migration for adding new column
            // database.execSQL("ALTER TABLE resumes ADD COLUMN new_column TEXT")
        }
    }

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Future migrations
        }
    }

    // Add more migrations as needed
    fun getAllMigrations(): Array<Migration> {
        return arrayOf(
            MIGRATION_1_2,
            MIGRATION_2_3
        )
    }
}

// Database Provider for dependency injection
class DatabaseProvider {

    companion object {
        fun provideDatabase(
            context: android.content.Context
        ): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                AppDatabase.DATABASE_NAME
            )
                .addMigrations(*DatabaseMigrations.getAllMigrations())
                .fallbackToDestructiveMigration() // Only for development
                .build()
        }

        fun provideResumeDao(database: AppDatabase): ResumeDao {
            return database.resumeDao()
        }
    }
}

// Database Callback for initial setup
class AppDatabaseCallback : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        // Perform any initial setup here
        // For example, insert default templates or sample data
    }

    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        // Perform any setup that should happen every time database is opened
    }
}