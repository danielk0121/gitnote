package dev.danielk.gitnote.db

import androidx.room.*
import dev.danielk.gitnote.model.Note

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    suspend fun getAllNotes(): List<Note>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notes: List<Note>)

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT COUNT(*) FROM notes")
    suspend fun countNotes(): Int

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' ORDER BY updatedAt DESC")
    suspend fun searchNotes(query: String): List<Note>

    @Query("SELECT tags FROM notes")
    suspend fun getAllTagsList(): List<String>
}
