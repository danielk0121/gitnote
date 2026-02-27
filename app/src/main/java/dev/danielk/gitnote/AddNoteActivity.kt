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

        etContent = findViewById(R.id.etContent)
        btnCancel = findViewById(R.id.btnCancel)
        btnSave = findViewById(R.id.btnSave)
        btnAddImage = findViewById(R.id.btnAddImage)

        existingNote = intent.getSerializableExtra("note") as? Note
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        
        if (existingNote != null) {
            val note = existingNote!!
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
        val fullContent = etContent.text.toString().trim()

        if (fullContent.isEmpty()) {
            finish()
            return
        }

        // Front Matter에서 타이틀 추출
        var title = extractTitle(fullContent)
        if (title.isBlank()) {
            title = "제목 없음"
        }

        // Front Matter에서 본문만 추출 (DB 저장용)
        val contentOnly = extractContentOnly(fullContent)

        val now = System.currentTimeMillis()
        val note = if (existingNote != null) {
            existingNote!!.copy(
                title = title,
                content = contentOnly,
                updatedAt = now
            )
        } else {
            Note(
                title = title,
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

    private fun extractTitle(fullContent: String): String {
        val firstDashIndex = fullContent.indexOf("---")
        if (firstDashIndex != 0) return ""

        val secondDashIndex = fullContent.indexOf("---", 3)
        if (secondDashIndex == -1) return ""

        val frontMatter = fullContent.substring(3, secondDashIndex)
        val lines = frontMatter.lines()
        for (line in lines) {
             if (line.trim().startsWith("title:")) {
                var titleValue = line.substringAfter("title:").trim()
                if (titleValue.startsWith("\"") && titleValue.endsWith("\"")) {
                    titleValue = titleValue.substring(1, titleValue.length - 1)
                }
                return titleValue.replace("\\\"", "\"")
            }
        }
        return ""
    }

    private fun extractContentOnly(fullContent: String): String {
        val firstDashIndex = fullContent.indexOf("---")
        if (firstDashIndex == 0) {
            val secondDashIndex = fullContent.indexOf("---", 3)
            if (secondDashIndex != -1) {
                return fullContent.substring(secondDashIndex + 3).trim()
            }
        }
        return fullContent
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
