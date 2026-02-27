package dev.danielk.gitnote

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.danielk.gitnote.model.Note

class AddNoteActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etContent: EditText
    private lateinit var fabSave: FloatingActionButton
    private lateinit var btnAddImage: ImageButton
    private lateinit var ivNoteImage: ImageView
    private var existingNote: Note? = null
    private var selectedImageUri: String? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // Take persistable URI permission
            contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            selectedImageUri = it.toString()
            ivNoteImage.visibility = View.VISIBLE
            ivNoteImage.load(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        etTitle = findViewById(R.id.etTitle)
        etContent = findViewById(R.id.etContent)
        fabSave = findViewById(R.id.fabSave)
        btnAddImage = findViewById(R.id.btnAddImage)
        ivNoteImage = findViewById(R.id.ivNoteImage)

        existingNote = intent.getSerializableExtra("note") as? Note
        existingNote?.let {
            etTitle.setText(it.title)
            etContent.setText(it.content)
            it.imageUri?.let { uri ->
                selectedImageUri = uri
                ivNoteImage.visibility = View.VISIBLE
                ivNoteImage.load(uri)
            }
        }

        findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener {
            finish()
        }

        btnAddImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        fabSave.setOnClickListener {
            saveNote()
        }
    }

    private fun saveNote() {
        val title = etTitle.text.toString().trim()
        val content = etContent.text.toString().trim()

        if (title.isEmpty()) {
            etTitle.error = "Title is required"
            return
        }

        val note = existingNote?.copy(title = title, content = content, imageUri = selectedImageUri)
            ?: Note(title = title, content = content, imageUri = selectedImageUri)

        val intent = Intent().apply {
            putExtra("note", note)
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
