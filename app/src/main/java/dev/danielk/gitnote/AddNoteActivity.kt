package dev.danielk.gitnote

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import dev.danielk.gitnote.model.Note
import java.io.File
import java.io.FileOutputStream

class AddNoteActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etContent: EditText
    private lateinit var btnCancel: Button
    private lateinit var btnSave: Button
    private lateinit var btnAddImage: ImageButton
    private var existingNote: Note? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            copyImageToInternalStorage(it)?.let { internalPath ->
                insertImageMarkdown(internalPath)
            }
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

        existingNote = intent.getSerializableExtra("note") as? Note
        existingNote?.let {
            etTitle.setText(it.title)
            etContent.setText(it.content)
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

    private fun copyImageToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val fileName = "img_${System.currentTimeMillis()}.jpg"
            val file = File(filesDir, fileName)
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun insertImageMarkdown(path: String) {
        val markdown = "\n![image]($path)\n"
        val start = etContent.selectionStart
        val end = etContent.selectionEnd
        etContent.text.replace(start, end, markdown)
    }

    private fun saveNote() {
        val title = etTitle.text.toString().trim()
        val content = etContent.text.toString().trim()

        if (content.isEmpty() && title.isEmpty()) {
            finish()
            return
        }

        val note = existingNote?.copy(
            title = title,
            content = content,
            timestamp = System.currentTimeMillis()
        ) ?: Note(
            title = title,
            content = content,
            timestamp = System.currentTimeMillis()
        )
        
        val intent = Intent().apply {
            putExtra("note", note)
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
