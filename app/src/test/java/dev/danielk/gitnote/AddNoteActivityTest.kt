package dev.danielk.gitnote

import android.content.Intent
import android.widget.EditText
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.danielk.gitnote.model.Note
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [33])
class AddNoteActivityTest {

    @Test
    fun testAddNoteActivityLoadsExistingNote() {
        val note = Note(id = 1, title = "Original Title", content = "Original Content")
        val intent = Intent(ApplicationProvider.getApplicationContext(), AddNoteActivity::class.java).apply {
            putExtra("note", note)
        }

        ActivityScenario.launch<AddNoteActivity>(intent).use { scenario ->
            scenario.onActivity { activity ->
                val etTitle = activity.findViewById<EditText>(R.id.etTitle)
                val etContent = activity.findViewById<EditText>(R.id.etContent)
                
                assertEquals("Original Title", etTitle.text.toString())
                assertEquals("Original Content", etContent.text.toString())
            }
        }
    }
}
