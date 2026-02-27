package dev.danielk.gitnote

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
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
    private lateinit var btnCancel: Button
    private lateinit var btnSave: Button
    private lateinit var btnAddImage: ImageButton
    private lateinit var ivNoteImage: ImageView
    private var existingNote: Note? = null
    private var selectedImageUri: String? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            try {
                contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: SecurityException) {
                // Ignore if we can't take permission, though it might break later
            }
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
        btnCancel = findViewById(R.id.btnCancel)
        btnSave = findViewById(R.id.btnSave)
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

        btnCancel.setOnClickListener {
            finish()
        }

        btnSave.setOnClickListener {
            saveNote()
        }

        btnAddImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }

    private fun saveNote() {
        val title = etTitle.text.toString().trim()
        val content = etContent.text.toString().trim()

        if (content.isEmpty() && title.isEmpty()) {
            finish() // Nothing to save
            return
        }

        // We allow empty title, it will show as "제목 없음" in the list
        val note = existingNote?.copy(
            title = title,
            content = content,
            imageUri = selectedImageUri,
            timestamp = System.currentTimeMillis()
        ) ?: Note(
            title = title,
            content = content,
            imageUri = selectedImageUri,
            timestamp = System.currentTimeMillis()
        )

        // Save to DB directly if it's an update, or pass back to MainActivity/ViewNoteActivity
        // Actually, to be consistent with MainActivity's current logic, we pass it back.
        // But MainActivity also needs to save it.
        
        val intent = Intent().apply {
            putExtra("note", note)
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
