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
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class AddNoteActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etContent: EditText
    private lateinit var btnCancel: Button
    private lateinit var btnSave: Button
    private lateinit var btnAddImage: ImageButton
    private var existingNote: Note? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

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
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        
        if (existingNote != null) {
            val note = existingNote!!
            etTitle.setText(note.title)
            etContent.setText(readFileContent(note.fileName))
            toolbar.title = "Edit Mode"
        } else {
            toolbar.title = "New Note"
            // 신규 작성 시 기본 Front Matter 템플릿 제공
            val now = System.currentTimeMillis()
            val template = """
                ---
                title: ""
                author: ""
                created_at: ${dateFormat.format(Date(now))}
                updated_at: ${dateFormat.format(Date(now))}
                ---
                
            """.trimIndent()
            etContent.setText(template)
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

    private fun readFileContent(fileName: String): String {
        val file = File(filesDir, fileName)
        if (!file.exists()) return ""
        return try {
            val inputStream = FileInputStream(file)
            val content = inputStream.bufferedReader().use { it.readText() }
            inputStream.close()
            content
        } catch (e: Exception) {
            e.printStackTrace()
            ""
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
        val titleFromInput = etTitle.text.toString().trim()
        val fullContent = etContent.text.toString().trim()

        if (fullContent.isEmpty() && titleFromInput.isEmpty()) {
            finish()
            return
        }

        // Front Matter에서 본문만 추출 (DB 저장용)
        val contentOnly = extractContentOnly(fullContent)
        // 만약 Front Matter 내부에 title이 있다면 그것을 우선할 수도 있지만, 
        // 여기서는 상단 제목 입력창(etTitle)을 기준으로 DB의 title을 관리합니다.

        val now = System.currentTimeMillis()
        val note = if (existingNote != null) {
            existingNote!!.copy(
                title = titleFromInput,
                content = contentOnly,
                updatedAt = now
            )
        } else {
            Note(
                title = titleFromInput,
                content = contentOnly,
                fileName = "${UUID.randomUUID()}.md",
                createdAt = now,
                updatedAt = now
            )
        }

        // 파일에는 에디터의 전체 내용(수정된 Front Matter 포함)을 그대로 저장합니다.
        writeFullContentToFile(note.fileName, fullContent)
        
        val intent = Intent().apply {
            putExtra("note", note)
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun extractContentOnly(fullContent: String): String {
        val parts = fullContent.split("---")
        return if (parts.size >= 3) {
            // Front Matter가 있는 경우
            parts.subList(2, parts.size).joinToString("---").trim()
        } else {
            fullContent
        }
    }

    private fun writeFullContentToFile(fileName: String, content: String) {
        try {
            val file = File(filesDir, fileName)
            val outputStream = FileOutputStream(file)
            outputStream.write(content.toByteArray())
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
