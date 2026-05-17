package com.thedigialex.inventory.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.thedigialex.inventory.database.entity.BudgetEntry

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budget_entries WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun getEntriesForMonth(start: Long, end: Long): LiveData<List<BudgetEntry>>

    @Insert
    suspend fun insert(entry: BudgetEntry)

    @Delete
    suspend fun delete(entry: BudgetEntry)
}
