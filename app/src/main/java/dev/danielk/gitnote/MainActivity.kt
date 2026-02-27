package dev.danielk.gitnote

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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
    private lateinit var searchView: androidx.appcompat.widget.SearchView
    private lateinit var toolbar: Toolbar
    private lateinit var btnSearch: ImageButton
    private lateinit var btnList: ImageButton
    private lateinit var btnSettings: ImageButton
    private lateinit var db: AppDatabase
    private lateinit var noteAdapter: NoteAdapter
    private val notes = mutableListOf<Note>()
    private var currentSortOrder = "date_desc"

    private val noteResultLauncher: ActivityResultLauncher<Intent> =
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
        searchView = findViewById(R.id.searchView)
        toolbar = findViewById(R.id.toolbar)
        btnSearch = findViewById(R.id.btnSearch)
        btnList = findViewById(R.id.btnList)
        btnSettings = findViewById(R.id.btnSettings)

        setupRecyclerView()
        setupSearchView()
        setupTopBar()
        loadNotes()
    }

    private fun setupTopBar() {
        btnSearch.setOnClickListener {
            if (searchView.visibility == View.VISIBLE) {
                searchView.visibility = View.GONE
                searchView.setQuery("", false)
                loadNotes()
            } else {
                searchView.visibility = View.VISIBLE
                searchView.requestFocus()
            }
        }

        btnList.setOnClickListener {
            showSortDialog()
        }

        btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showSortDialog() {
        val options = arrayOf("Date (Newest)", "Date (Oldest)", "Title (A-Z)", "Title (Z-A)")
        AlertDialog.Builder(this)
            .setTitle("Sort by")
            .setItems(options) { _, which ->
                currentSortOrder = when (which) {
                    0 -> "date_desc"
                    1 -> "date_asc"
                    2 -> "title_asc"
                    3 -> "title_desc"
                    else -> "date_desc"
                }
                sortAndDisplayNotes()
            }
            .show()
    }

    private fun sortAndDisplayNotes() {
        val sortedList = when (currentSortOrder) {
            "date_desc" -> notes.sortedByDescending { it.timestamp }
            "date_asc" -> notes.sortedBy { it.timestamp }
            "title_asc" -> notes.sortedBy { if (it.title.isEmpty()) "제목 없음" else it.title }
            "title_desc" -> notes.sortedByDescending { if (it.title.isEmpty()) "제목 없음" else it.title }
            else -> notes
        }
        notes.clear()
        notes.addAll(sortedList)
        noteAdapter.notifyDataSetChanged()
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    searchNotes(newText)
                }
                return true
            }
        })
    }

    private fun setupRecyclerView() {
        noteAdapter = NoteAdapter(
            notes,
            onNoteClick = { note ->
                val intent = Intent(this, ViewNoteActivity::class.java).apply {
                    putExtra("note", note)
                }
                noteResultLauncher.launch(intent)
            },
            onNoteLongClick = { note ->
                showDeleteDialog(note)
            }
        )
        recyclerView.adapter = noteAdapter

        fabAdd.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            noteResultLauncher.launch(intent)
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
                sortAndDisplayNotes()
                syncWithGitHub()
            }
        }
    }

    private fun searchNotes(query: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val filteredNotes = if (query.isEmpty()) {
                db.noteDao().getAllNotes()
            } else {
                db.noteDao().searchNotes(query)
            }
            withContext(Dispatchers.Main) {
                notes.clear()
                notes.addAll(filteredNotes)
                sortAndDisplayNotes()
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
        val sharedPref = getSharedPreferences("gitnote_prefs", MODE_PRIVATE)
        if (!sharedPref.getBoolean("github_sync_enabled", false)) {
            return
        }

        lifecycleScope.launch {
            syncLayout.visibility = View.VISIBLE
            delay(2000) // Simulate network delay
            syncLayout.visibility = View.GONE
            Toast.makeText(this@MainActivity, "GitHub synchronization complete (Demo)", Toast.LENGTH_SHORT).show()
        }
    }
}
