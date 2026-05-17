package com.thedigialex.inventory.repository

import com.thedigialex.inventory.database.dao.FeatureDao
import com.thedigialex.inventory.database.dao.ProjectDao
import com.thedigialex.inventory.database.dao.TaskDao
import com.thedigialex.inventory.database.entity.Feature
import com.thedigialex.inventory.database.entity.Project
import com.thedigialex.inventory.database.entity.Task

class TaskRepository(
    private val projectDao: ProjectDao,
    private val featureDao: FeatureDao,
    private val taskDao: TaskDao
) {
    fun getAllProjects() = projectDao.getAllProjects()
    fun getFeaturesForProject(id: Int) = featureDao.getFeaturesForProject(id)
    fun getTasksForFeature(id: Int) = taskDao.getTasksForFeature(id)
    fun getTasksForDateRange(start: Long, end: Long) = taskDao.getTasksForDateRange(start, end)

    suspend fun insertProject(p: Project) = projectDao.insert(p)
    suspend fun updateProject(p: Project) = projectDao.update(p)
    suspend fun deleteProject(p: Project) = projectDao.delete(p)
    suspend fun insertFeature(f: Feature) = featureDao.insert(f)
    suspend fun updateFeature(f: Feature) = featureDao.update(f)
    suspend fun deleteFeature(f: Feature) = featureDao.delete(f)
    suspend fun insertTask(t: Task) = taskDao.insert(t)
    suspend fun updateTask(t: Task) = taskDao.update(t)
    suspend fun deleteTask(t: Task) = taskDao.delete(t)
}
