package dev.danielk.gitnote

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.danielk.gitnote.adapter.NoteAdapter
import dev.danielk.gitnote.db.AppDatabase
import dev.danielk.gitnote.model.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var syncLayout: LinearLayout
    private lateinit var db: AppDatabase
    private lateinit var noteAdapter: NoteAdapter
    private val notes = mutableListOf<Note>()

    private val addNoteResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val note = result.data?.getSerializableExtra("note") as? Note
                note?.let { saveNote(it) }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = AppDatabase.getDatabase(this)
        recyclerView = findViewById(R.id.recyclerView)
        fabAdd = findViewById(R.id.fabAdd)
        syncLayout = findViewById(R.id.syncLayout)

        setupRecyclerView()
        loadNotes()
    }

    private fun setupRecyclerView() {
        noteAdapter = NoteAdapter(
            notes,
            onNoteClick = { note ->
                val intent = Intent(this, AddNoteActivity::class.java).apply {
                    putExtra("note", note)
                }
                addNoteResultLauncher.launch(intent)
            },
            onNoteLongClick = { note ->
                showDeleteDialog(note)
            }
        )
        recyclerView.adapter = noteAdapter

        fabAdd.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            addNoteResultLauncher.launch(intent)
        }
    }

    private fun showDeleteDialog(note: Note) {
        AlertDialog.Builder(this)
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Delete") { _, _ ->
                deleteNote(note)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun loadNotes() {
        lifecycleScope.launch(Dispatchers.IO) {
            val loadedNotes = db.noteDao().getAllNotes()
            withContext(Dispatchers.Main) {
                notes.clear()
                notes.addAll(loadedNotes)
                noteAdapter.notifyDataSetChanged()
                syncWithGitHub()
            }
        }
    }

    private fun saveNote(note: Note) {
        lifecycleScope.launch(Dispatchers.IO) {
            if (note.id == 0) {
                db.noteDao().insertNote(note)
            } else {
                db.noteDao().updateNote(note)
            }
            loadNotes()
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Note saved locally", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteNote(note: Note) {
        lifecycleScope.launch(Dispatchers.IO) {
            db.noteDao().deleteNote(note)
            loadNotes()
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Note deleted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun syncWithGitHub() {
        lifecycleScope.launch {
            syncLayout.visibility = View.VISIBLE
            delay(2000) // Simulate network delay
            syncLayout.visibility = View.GONE
            Toast.makeText(this@MainActivity, "GitHub synchronization complete (Demo)", Toast.LENGTH_SHORT).show()
        }
    }
}
