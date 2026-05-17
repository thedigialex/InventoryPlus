package com.thedigialex.inventory.ui.notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.thedigialex.inventory.database.AppDatabase
import com.thedigialex.inventory.database.entity.Note
import com.thedigialex.inventory.repository.NoteRepository
import kotlinx.coroutines.launch

class NoteViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = NoteRepository(AppDatabase.getInstance(app).noteDao())

    val notes = repo.getAllNotes()

    fun insert(note: Note) = viewModelScope.launch { repo.insert(note) }
    fun update(note: Note) = viewModelScope.launch { repo.update(note) }
    fun delete(note: Note) = viewModelScope.launch { repo.delete(note) }
}
