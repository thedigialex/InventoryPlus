package com.thedigialex.inventory.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.thedigialex.inventory.database.entity.Project

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY name ASC")
    fun getAllProjects(): LiveData<List<Project>>

    @Insert
    suspend fun insert(project: Project)

    @Update
    suspend fun update(project: Project)

    @Delete
    suspend fun delete(project: Project)
}
