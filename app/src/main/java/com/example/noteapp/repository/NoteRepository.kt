package com.example.noteapp.repository

import androidx.lifecycle.LiveData
import com.example.noteapp.model.Note
import com.example.noteapp.model.data.NoteDao

class NoteRepository(private val noteDao: NoteDao) {
    val getAllNote: LiveData<List<Note>> = noteDao.getAllNotes()

    suspend fun addNote(note: Note) {
        noteDao.addNote(note)
    }

    suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note)
    }

    suspend fun updateNote(note: Note) {
        noteDao.updateNote(note)
    }
}