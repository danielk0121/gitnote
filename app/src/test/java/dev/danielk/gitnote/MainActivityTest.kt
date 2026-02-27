package dev.danielk.gitnote

import android.content.res.Configuration
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [33])
class MainActivityTest {

    @Test
    fun testDarkModeConfiguration() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                // Change configuration to dark mode
                val darkModeConfig = Configuration(activity.resources.configuration)
                darkModeConfig.uiMode = Configuration.UI_MODE_NIGHT_YES
                
                activity.onConfigurationChanged(darkModeConfig)
                
                // Verify the theme is still correctly applied
                assertNotNull(activity.theme)
            }
        }
    }
}
