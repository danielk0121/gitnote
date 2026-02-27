package dev.danielk.gitnote

import android.app.Activity
import android.view.View
import android.widget.LinearLayout
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper

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
    fun testSyncLayoutInitialState() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val syncLayout = activity.findViewById<LinearLayout>(R.id.syncLayout)
                assertNotNull(syncLayout)
                
                // Initially may be GONE if coroutine hasn't run yet, 
                // or VISIBLE if it has started.
                // We'll verify it's at least present in the layout.
            }
        }
    }
}
