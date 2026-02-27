package dev.danielk.gitnote

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
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
    private lateinit var btnDelete: ImageButton
    private lateinit var btnCloseSelection: ImageButton
    private lateinit var btnPull: ImageButton
    private lateinit var btnPush: ImageButton
    private lateinit var layoutSyncButtons: LinearLayout
    private lateinit var tvAppName: TextView
    private lateinit var tvRepoName: TextView
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
        btnDelete = findViewById(R.id.btnDelete)
        btnCloseSelection = findViewById(R.id.btnCloseSelection)
        btnPull = findViewById(R.id.btnPull)
        btnPush = findViewById(R.id.btnPush)
        layoutSyncButtons = findViewById(R.id.layoutSyncButtons)
        tvAppName = findViewById(R.id.tvAppName)
        tvRepoName = findViewById(R.id.tvRepoName)

        setupRecyclerView()
        setupSearchView()
        setupTopBar()
        loadNotes()
    }

    override fun onResume() {
        super.onResume()
        updateRepoStatus()
    }

    private fun updateRepoStatus() {
        val sharedPref = getSharedPreferences("gitnote_prefs", MODE_PRIVATE)
        val isSyncEnabled = sharedPref.getBoolean("github_sync_enabled", false)
        if (isSyncEnabled) {
            val repoName = sharedPref.getString("current_repo_name", "No repository")
            tvRepoName.text = repoName
            tvRepoName.visibility = View.VISIBLE
            layoutSyncButtons.visibility = View.VISIBLE
        } else {
            tvRepoName.visibility = View.GONE
            layoutSyncButtons.visibility = View.GONE
        }
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

        btnDelete.setOnClickListener {
            showBulkDeleteDialog()
        }

        btnCloseSelection.setOnClickListener {
            noteAdapter.setSelectionMode(false)
        }

        btnPull.setOnClickListener {
            showSyncDialog("Pull")
        }

        btnPush.setOnClickListener {
            showSyncDialog("Push")
        }
    }

    private fun showSyncDialog(action: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_sync_auth, null)
        val etPat = dialogView.findViewById<EditText>(R.id.etPat)

        AlertDialog.Builder(this)
            .setTitle("$action from GitHub")
            .setMessage("Please enter your Personal Access Token (PAT) for security. It will not be stored.")
            .setView(dialogView)
            .setPositiveButton(action) { _, _ ->
                val pat = etPat.text.toString().trim()
                if (pat.isNotEmpty()) {
                    simulateSync(action)
                } else {
                    Toast.makeText(this, "PAT is required", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun simulateSync(action: String) {
        lifecycleScope.launch {
            syncLayout.visibility = View.VISIBLE
            delay(2000)
            syncLayout.visibility = View.GONE
            Toast.makeText(this@MainActivity, "$action complete (Demo)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showBulkDeleteDialog() {
        val count = noteAdapter.getSelectedItems().size
        AlertDialog.Builder(this)
            .setTitle("Delete Notes")
            .setMessage("Are you sure you want to delete $count selected notes?")
            .setPositiveButton("Delete") { _, _ ->
                deleteSelectedNotes()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteSelectedNotes() {
        val selectedNotes = noteAdapter.getSelectedItems()
        lifecycleScope.launch(Dispatchers.IO) {
            for (note in selectedNotes) {
                db.noteDao().deleteNote(note)
                val file = File(filesDir, note.fileName)
                if (file.exists()) {
                    file.delete()
                }
            }
            withContext(Dispatchers.Main) {
                noteAdapter.setSelectionMode(false)
                loadNotes()
                Toast.makeText(this@MainActivity, "${selectedNotes.size} notes deleted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateSelectionUI(count: Int) {
        if (count > 0) {
            tvAppName.text = "$count selected"
            btnDelete.visibility = View.VISIBLE
            btnCloseSelection.visibility = View.VISIBLE
            btnSearch.visibility = View.GONE
            btnList.visibility = View.GONE
            btnSettings.visibility = View.GONE
            fabAdd.visibility = View.GONE
        } else {
            tvAppName.text = "gitnote"
            btnDelete.visibility = View.GONE
            btnCloseSelection.visibility = View.GONE
            btnSearch.visibility = View.VISIBLE
            btnList.visibility = View.VISIBLE
            btnSettings.visibility = View.VISIBLE
            fabAdd.visibility = View.VISIBLE
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
            "date_desc" -> notes.sortedByDescending { it.updatedAt }
            "date_asc" -> notes.sortedBy { it.updatedAt }
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
                showActionDialog(note)
            },
            onSelectionChanged = { count ->
                updateSelectionUI(count)
            }
        )
        recyclerView.adapter = noteAdapter

        fabAdd.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            noteResultLauncher.launch(intent)
        }
    }

    private fun showActionDialog(note: Note) {
        val options = arrayOf("Copy", "Delete")
        AlertDialog.Builder(this)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showCopyConfirmDialog(note)
                    1 -> showDeleteDialog(note)
                }
            }
            .show()
    }

    private fun showCopyConfirmDialog(note: Note) {
        AlertDialog.Builder(this)
            .setTitle("Copy Note")
            .setMessage("Do you want to copy this note?")
            .setPositiveButton("Yes") { _, _ ->
                copyNote(note)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun copyNote(note: Note) {
        val now = System.currentTimeMillis()
        val copiedNote = note.copy(
            id = 0,
            title = "copy ${note.title}",
            fileName = "${UUID.randomUUID()}.md",
            createdAt = now,
            updatedAt = now
        )
        
        lifecycleScope.launch(Dispatchers.IO) {
            db.noteDao().insertNote(copiedNote)
            // Create the file for the copy
            val header = """
                ---
                title: "${copiedNote.title.replace("\"", "\\\"")}"
                created_at: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(copiedNote.createdAt))}
                updated_at: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(copiedNote.updatedAt))}
                ---
                
            """.trimIndent()
            
            try {
                val file = File(filesDir, copiedNote.fileName)
                val outputStream = FileOutputStream(file)
                outputStream.write((header + copiedNote.content).toByteArray())
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            
            loadNotes()
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Note copied", Toast.LENGTH_SHORT).show()
            }
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
            val file = File(filesDir, note.fileName)
            if (file.exists()) {
                file.delete()
            }
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
