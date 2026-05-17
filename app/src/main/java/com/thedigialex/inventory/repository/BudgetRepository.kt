package com.thedigialex.inventory.repository

import com.thedigialex.inventory.database.dao.BudgetCategoryDao
import com.thedigialex.inventory.database.dao.BudgetDao
import com.thedigialex.inventory.database.dao.BudgetSubCategoryDao
import com.thedigialex.inventory.database.entity.BudgetCategory
import com.thedigialex.inventory.database.entity.BudgetEntry
import com.thedigialex.inventory.database.entity.BudgetSubCategory

class BudgetRepository(
    private val budgetDao: BudgetDao,
    private val categoryDao: BudgetCategoryDao,
    private val subCategoryDao: BudgetSubCategoryDao
) {
    fun getEntriesForMonth(start: Long, end: Long) = budgetDao.getEntriesForMonth(start, end)
    fun getAllCategories() = categoryDao.getAllCategories()
    fun getSubCategoriesFor(categoryId: Int) = subCategoryDao.getSubCategoriesForCategory(categoryId)
    fun getAllCategoryTotals() = subCategoryDao.getAllCategoryTotals()

    suspend fun getAllCategoriesOnce() = categoryDao.getAllCategoriesOnce()
    suspend fun getSubCategoriesOnce(categoryId: Int) = subCategoryDao.getSubCategoriesOnce(categoryId)

    suspend fun insert(entry: BudgetEntry) = budgetDao.insert(entry)
    suspend fun delete(entry: BudgetEntry) = budgetDao.delete(entry)

    suspend fun insertCategory(c: BudgetCategory): Long = categoryDao.insert(c)
    suspend fun deleteCategory(c: BudgetCategory) = categoryDao.delete(c)
    suspend fun insertSubCategory(s: BudgetSubCategory) = subCategoryDao.insert(s)
    suspend fun updateSubCategory(s: BudgetSubCategory) = subCategoryDao.update(s)
    suspend fun deleteSubCategory(s: BudgetSubCategory) = subCategoryDao.delete(s)
}
