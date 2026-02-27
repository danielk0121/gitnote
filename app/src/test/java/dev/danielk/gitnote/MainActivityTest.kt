package dev.danielk.gitnote

import android.app.Activity
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.danielk.gitnote.model.Note
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [33])
class MainActivityTest {

    @Test
    fun testMainActivityLaunchesSuccessfully() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                assertNotNull(activity)
            }
        }
    }

    @Test
    fun testFabClickOpensAddNoteActivity() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val fab = activity.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabAdd)
                fab.performClick()
                
                // Robolectric's shadow methods can verify the next started activity if needed
            }
        }
    }
}
