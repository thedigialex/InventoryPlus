package com.thedigialex.inventory.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.thedigialex.inventory.database.entity.Note

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY updatedDate DESC")
    fun getAllNotes(): LiveData<List<Note>>

    @Insert
    suspend fun insert(note: Note)

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)
}
