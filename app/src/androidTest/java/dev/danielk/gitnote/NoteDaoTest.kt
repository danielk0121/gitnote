package dev.danielk.gitnote

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.danielk.gitnote.db.AppDatabase
import dev.danielk.gitnote.db.NoteDao
import dev.danielk.gitnote.model.Note
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class NoteDaoTest {
    private lateinit var noteDao: NoteDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        noteDao = db.noteDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeNoteAndReadInList() = runBlocking {
        val note = Note(title = "Test Note", content = "Test Content")
        noteDao.insertNote(note)
        val allNotes = noteDao.getAllNotes()
        assertEquals(allNotes[0].title, note.title)
    }

    @Test
    @Throws(Exception::class)
    fun deleteNote() = runBlocking {
        val note = Note(id = 1, title = "Delete Me", content = "Content")
        noteDao.insertNote(note)
        val insertedNote = noteDao.getAllNotes()[0]
        noteDao.deleteNote(insertedNote)
        val allNotes = noteDao.getAllNotes()
        assertTrue(allNotes.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun searchNotes() = runBlocking {
        val note1 = Note(title = "Banana", content = "Yellow fruit")
        val note2 = Note(title = "Apple", content = "Red banana") // content contains 'banana'
        noteDao.insertNote(note1)
        noteDao.insertNote(note2)

        val results = noteDao.searchNotes("Banana")
        assertEquals(2, results.size) // both title/content match
    }

    @Test
    @Throws(Exception::class)
    fun insertAllNotes() = runBlocking {
        val notes = listOf(
            Note(title = "Note 1", content = "Content 1"),
            Note(title = "Note 2", content = "Content 2"),
            Note(title = "Note 3", content = "Content 3")
        )
        noteDao.insertAll(notes)
        val allNotes = noteDao.getAllNotes()
        assertEquals(3, allNotes.size)
    }
}
