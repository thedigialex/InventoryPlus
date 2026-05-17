package com.thedigialex.inventory.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.thedigialex.inventory.database.entity.Task

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE featureId = :featureId ORDER BY priority DESC, dueDate ASC")
    fun getTasksForFeature(featureId: Int): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE dueDate BETWEEN :start AND :end")
    fun getTasksForDateRange(start: Long, end: Long): LiveData<List<Task>>

    @Insert
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}
