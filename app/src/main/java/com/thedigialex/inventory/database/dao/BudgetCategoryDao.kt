package com.thedigialex.inventory.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.thedigialex.inventory.database.entity.BudgetCategory

@Dao
interface BudgetCategoryDao {
    @Query("SELECT * FROM budget_categories ORDER BY name ASC")
    fun getAllCategories(): LiveData<List<BudgetCategory>>

    @Query("SELECT * FROM budget_categories ORDER BY name ASC")
    suspend fun getAllCategoriesOnce(): List<BudgetCategory>

    @Insert
    suspend fun insert(category: BudgetCategory): Long

    @Delete
    suspend fun delete(category: BudgetCategory)
}
