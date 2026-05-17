package com.thedigialex.inventory.ui.tasks

import android.app.Application
import androidx.lifecycle.*
import com.thedigialex.inventory.database.AppDatabase
import com.thedigialex.inventory.database.entity.Feature
import com.thedigialex.inventory.database.entity.Project
import com.thedigialex.inventory.database.entity.Task
import com.thedigialex.inventory.repository.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel(app: Application) : AndroidViewModel(app) {
    private val db = AppDatabase.getInstance(app)
    private val repo = TaskRepository(db.projectDao(), db.featureDao(), db.taskDao())

    val projects: LiveData<List<Project>> = repo.getAllProjects()

    private val _selectedProject = MutableLiveData<Project>()
    val selectedProject: LiveData<Project> = _selectedProject

    private val _selectedFeature = MutableLiveData<Feature>()
    val selectedFeature: LiveData<Feature> = _selectedFeature

    val features: LiveData<List<Feature>> = _selectedProject.switchMap {
        repo.getFeaturesForProject(it.id)
    }

    val tasks: LiveData<List<Task>> = _selectedFeature.switchMap {
        repo.getTasksForFeature(it.id)
    }

    fun selectProject(p: Project) { _selectedProject.value = p }
    fun selectFeature(f: Feature) { _selectedFeature.value = f }

    fun insertProject(p: Project) = viewModelScope.launch { repo.insertProject(p) }
    fun updateProject(p: Project) = viewModelScope.launch { repo.updateProject(p) }
    fun deleteProject(p: Project) = viewModelScope.launch { repo.deleteProject(p) }
    fun insertFeature(f: Feature) = viewModelScope.launch { repo.insertFeature(f) }
    fun updateFeature(f: Feature) = viewModelScope.launch { repo.updateFeature(f) }
    fun deleteFeature(f: Feature) = viewModelScope.launch { repo.deleteFeature(f) }
    fun insertTask(t: Task) = viewModelScope.launch { repo.insertTask(t) }
    fun updateTask(t: Task) = viewModelScope.launch { repo.updateTask(t) }
    fun deleteTask(t: Task) = viewModelScope.launch { repo.deleteTask(t) }

    fun getTasksForDateRange(start: Long, end: Long) = repo.getTasksForDateRange(start, end)
}
