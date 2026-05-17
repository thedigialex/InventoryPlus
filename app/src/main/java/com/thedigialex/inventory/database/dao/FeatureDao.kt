package com.thedigialex.inventory.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.thedigialex.inventory.database.entity.Feature

@Dao
interface FeatureDao {
    @Query("SELECT * FROM features WHERE projectId = :projectId ORDER BY name ASC")
    fun getFeaturesForProject(projectId: Int): LiveData<List<Feature>>

    @Insert
    suspend fun insert(feature: Feature)

    @Update
    suspend fun update(feature: Feature)

    @Delete
    suspend fun delete(feature: Feature)
}
