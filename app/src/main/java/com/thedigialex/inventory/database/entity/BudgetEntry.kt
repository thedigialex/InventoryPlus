package com.thedigialex.inventory.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budget_entries")
data class BudgetEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subCategoryId: Int = 0,
    val categoryName: String,
    val subCategoryName: String = "",
    val amount: Double,
    val description: String = "",
    val date: Long,
    val type: String
)
