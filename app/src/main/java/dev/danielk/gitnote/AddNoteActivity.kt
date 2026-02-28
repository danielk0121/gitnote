package dev.danielk.gitnote

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.MultiAutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dev.danielk.gitnote.db.AppDatabase
import dev.danielk.gitnote.model.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class AddNoteActivity : AppCompatActivity() {

    private lateinit var etContent: EditText
    private lateinit var etTags: MultiAutoCompleteTextView
    private lateinit var btnCancel: Button
    private lateinit var btnSave: Button
    private lateinit var btnAddImage: ImageButton
    private lateinit var btnMeta: ImageButton
    private lateinit var db: AppDatabase
    private var existingNote: Note? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private var currentFrontMatterMap: MutableMap<String, String> = mutableMapOf()

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

        db = AppDatabase.getDatabase(this)
        etContent = findViewById(R.id.etContent)
        etTags = findViewById(R.id.etTags)
        btnCancel = findViewById(R.id.btnCancel)
        btnSave = findViewById(R.id.btnSave)
        btnAddImage = findViewById(R.id.btnAddImage)
        btnMeta = findViewById(R.id.btnMeta)

        setupTagAutoComplete()

        // 스크롤 시 커서가 화면 밖으로 나가도 강제로 스크롤을 멈추지 않도록 설정
        etContent.movementMethod = android.text.method.ScrollingMovementMethod()
        
        // 키보드가 올라올 때 레이아웃이 깨지면서 커서가 튀는 것을 방지
        window.setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        existingNote = intent.getSerializableExtra("note") as? Note
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        
        if (existingNote != null) {
            val note = existingNote!!
            val fullContent = readFileContent(note.fileName)
            parseFrontMatter(fullContent)
            etContent.setText(stripFrontMatter(fullContent))
            etTags.setText(note.tags)
            toolbar.title = "Edit Mode"
        } else {
            toolbar.title = "New Note"
            val now = System.currentTimeMillis()
            currentFrontMatterMap["author"] = ""
            currentFrontMatterMap["created_at"] = dateFormat.format(Date(now))
            currentFrontMatterMap["updated_at"] = dateFormat.format(Date(now))
            etContent.setText("")
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

        btnMeta.setOnClickListener {
            showMetaEditDialog()
        }
    }

    private fun setupTagAutoComplete() {
        etTags.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
        lifecycleScope.launch(Dispatchers.IO) {
            val allTagsList = db.noteDao().getAllTagsList()
            val uniqueTags = allTagsList.flatMap { it.split(",") }
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .distinct()
            
            withContext(Dispatchers.Main) {
                val adapter = ArrayAdapter(
                    this@AddNoteActivity,
                    android.R.layout.simple_dropdown_item_1line,
                    uniqueTags
                )
                etTags.setAdapter(adapter)
            }
        }
    }

    private fun parseFrontMatter(fullContent: String) {
        currentFrontMatterMap.clear()
        val firstDashIndex = fullContent.indexOf("---")
        if (firstDashIndex != 0) return

        val secondDashIndex = fullContent.indexOf("---", 3)
        if (secondDashIndex == -1) return

        val frontMatter = fullContent.substring(3, secondDashIndex)
        frontMatter.lines().forEach { line ->
            if (line.contains(":")) {
                val key = line.substringBefore(":").trim()
                var value = line.substringAfter(":").trim()
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length - 1)
                }
                currentFrontMatterMap[key] = value.replace("\\\"", "\"")
            }
        }
    }

    private fun stripFrontMatter(fullContent: String): String {
        val firstDashIndex = fullContent.indexOf("---")
        if (firstDashIndex == 0) {
            val secondDashIndex = fullContent.indexOf("---", 3)
            if (secondDashIndex != -1) {
                return fullContent.substring(secondDashIndex + 3).trim()
            }
        }
        return fullContent
    }

    private fun generateFrontMatter(): String {
        val sb = StringBuilder("---\n")
        currentFrontMatterMap.forEach { (key, value) ->
            val escapedValue = value.replace("\"", "\\\"")
            sb.append("$key: \"$escapedValue\"\n")
        }
        sb.append("---\n")
        return sb.toString()
    }

    private fun showMetaEditDialog() {
        val sb = StringBuilder()
        currentFrontMatterMap.forEach { (key, value) ->
            sb.append("$key: $value\n")
        }

        val editText = EditText(this)
        editText.setText(sb.toString())
        editText.setPadding(32, 32, 32, 32)

        AlertDialog.Builder(this)
            .setTitle("Edit Metadata")
            .setView(editText)
            .setPositiveButton("OK") { _, _ ->
                val lines = editText.text.toString().lines()
                currentFrontMatterMap.clear()
                lines.forEach { line ->
                    if (line.contains(":")) {
                        val key = line.substringBefore(":").trim()
                        val value = line.substringAfter(":").trim()
                        currentFrontMatterMap[key] = value
                    }
                }
                Toast.makeText(this, "Metadata updated", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
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
        val content = etContent.text.toString().trim()
        val tags = etTags.text.toString().trim()

        if (content.isEmpty()) {
            finish()
            return
        }

        // Simplification: First line is title
        val firstLine = content.lines().firstOrNull { it.isNotBlank() } ?: "제목 없음"
        val title = if (firstLine.length > 50) firstLine.substring(0, 47) + "..." else firstLine

        currentFrontMatterMap["title"] = title
        currentFrontMatterMap["tags"] = tags
        val now = System.currentTimeMillis()
        currentFrontMatterMap["updated_at"] = dateFormat.format(Date(now))

        val note = if (existingNote != null) {
            existingNote!!.copy(
                title = title,
                content = content,
                tags = tags,
                updatedAt = now
            )
        } else {
            Note(
                title = title,
                content = content,
                fileName = "${UUID.randomUUID()}.md",
                tags = tags,
                createdAt = now,
                updatedAt = now
            )
        }

        // Add Front Matter to file content
        val fullFileContent = generateFrontMatter() + "\n" + content
        writeFullContentToFile(note.fileName, fullFileContent)
        
        val intent = Intent().apply {
            putExtra("note", note)
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
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
