package com.thedigialex.inventory.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.thedigialex.inventory.database.dao.*
import com.thedigialex.inventory.database.entity.*

@Database(
    entities = [
        BudgetEntry::class, BudgetCategory::class, BudgetSubCategory::class,
        Project::class, Feature::class, Task::class, Note::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun budgetDao(): BudgetDao
    abstract fun budgetCategoryDao(): BudgetCategoryDao
    abstract fun budgetSubCategoryDao(): BudgetSubCategoryDao
    abstract fun projectDao(): ProjectDao
    abstract fun featureDao(): FeatureDao
    abstract fun taskDao(): TaskDao
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "inventory.db"
                )
                .fallbackToDestructiveMigration()
                .build().also { INSTANCE = it }
            }
    }
}
