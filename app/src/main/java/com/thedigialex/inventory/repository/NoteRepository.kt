package com.thedigialex.inventory.repository

import com.thedigialex.inventory.database.dao.NoteDao
import com.thedigialex.inventory.database.entity.Note

class NoteRepository(private val dao: NoteDao) {
    fun getAllNotes() = dao.getAllNotes()
    suspend fun insert(note: Note) = dao.insert(note)
    suspend fun update(note: Note) = dao.update(note)
    suspend fun delete(note: Note) = dao.delete(note)
}
