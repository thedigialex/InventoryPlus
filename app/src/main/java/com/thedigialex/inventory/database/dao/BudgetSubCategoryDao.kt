package com.thedigialex.inventory.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.thedigialex.inventory.database.entity.BudgetSubCategory

@Dao
interface BudgetSubCategoryDao {
    @Query("SELECT * FROM budget_sub_categories WHERE categoryId = :categoryId ORDER BY name ASC")
    fun getSubCategoriesForCategory(categoryId: Int): LiveData<List<BudgetSubCategory>>

    @Query("SELECT * FROM budget_sub_categories WHERE categoryId = :categoryId ORDER BY name ASC")
    suspend fun getSubCategoriesOnce(categoryId: Int): List<BudgetSubCategory>

    @Insert
    suspend fun insert(s: BudgetSubCategory)

    @Update
    suspend fun update(s: BudgetSubCategory)

    @Delete
    suspend fun delete(s: BudgetSubCategory)
}
