package dev.danielk.gitnote

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import dev.danielk.gitnote.db.AppDatabase
import dev.danielk.gitnote.model.Note
import io.noties.markwon.Markwon
import io.noties.markwon.image.coil.CoilImagesPlugin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewNoteActivity : AppCompatActivity() {

    private lateinit var tvTitle: TextView
    private lateinit var tvContent: TextView
    private lateinit var ivNoteImage: ImageView
    private lateinit var btnEdit: ImageButton
    private lateinit var markwon: Markwon
    private lateinit var db: AppDatabase
    private var note: Note? = null

    private val editNoteResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val updatedNote = result.data?.getSerializableExtra("note") as? Note
                updatedNote?.let {
                    saveUpdatedNote(it)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_note)

        db = AppDatabase.getDatabase(this)
        tvTitle = findViewById(R.id.tvTitle)
        tvContent = findViewById(R.id.tvContent)
        ivNoteImage = findViewById(R.id.ivNoteImage)
        btnEdit = findViewById(R.id.btnEdit)

        markwon = Markwon.builder(this)
            .usePlugin(CoilImagesPlugin.create(this))
            .build()

        note = intent.getSerializableExtra("note") as? Note

        updateUI()

        btnEdit.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java).apply {
                putExtra("note", note)
            }
            editNoteResultLauncher.launch(intent)
        }

        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).setNavigationOnClickListener {
            finish()
        }
    }

    private fun saveUpdatedNote(updatedNote: Note) {
        lifecycleScope.launch(Dispatchers.IO) {
            db.noteDao().updateNote(updatedNote)
            withContext(Dispatchers.Main) {
                note = updatedNote
                updateUI()
                // Set result to OK so MainActivity knows it needs to refresh
                val resultIntent = Intent().apply {
                    putExtra("note", updatedNote)
                }
                setResult(Activity.RESULT_OK, resultIntent)
            }
        }
    }

    private fun updateUI() {
        note?.let {
            tvTitle.text = if (it.title.isEmpty()) "제목 없음" else it.title
            markwon.setMarkdown(tvContent, it.content)

            if (!it.imageUri.isNullOrEmpty()) {
                ivNoteImage.visibility = View.VISIBLE
                ivNoteImage.load(Uri.parse(it.imageUri))
            } else {
                ivNoteImage.visibility = View.GONE
            }
        }
    }
}
