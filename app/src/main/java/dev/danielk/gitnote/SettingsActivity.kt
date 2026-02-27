package dev.danielk.gitnote

import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var switchGitHubSync: SwitchMaterial
    private lateinit var radioGroupTheme: RadioGroup
    private lateinit var radioSystemDefault: RadioButton
    private lateinit var radioLight: RadioButton
    private lateinit var radioDark: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        switchGitHubSync = findViewById(R.id.switchGitHubSync)
        radioGroupTheme = findViewById(R.id.radioGroupTheme)
        radioSystemDefault = findViewById(R.id.radioSystemDefault)
        radioLight = findViewById(R.id.radioLight)
        radioDark = findViewById(R.id.radioDark)

        findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener {
            finish()
        }

        loadSettings()
        setupListeners()
    }

    private fun loadSettings() {
        // Load theme setting
        val themeMode = AppCompatDelegate.getDefaultNightMode()
        when (themeMode) {
            AppCompatDelegate.MODE_NIGHT_NO -> radioLight.isChecked = true
            AppCompatDelegate.MODE_NIGHT_YES -> radioDark.isChecked = true
            else -> radioSystemDefault.isChecked = true
        }
        
        // Load GitHub Sync setting (dummy)
        val sharedPref = getSharedPreferences("gitnote_prefs", MODE_PRIVATE)
        val isSyncEnabled = sharedPref.getBoolean("github_sync_enabled", false)
        switchGitHubSync.isChecked = isSyncEnabled
    }

    private fun setupListeners() {
        radioGroupTheme.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioSystemDefault -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
                R.id.radioLight -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                R.id.radioDark -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }
        }

        switchGitHubSync.setOnCheckedChangeListener { _, isChecked ->
            val sharedPref = getSharedPreferences("gitnote_prefs", MODE_PRIVATE)
            with(sharedPref.edit()) {
                putBoolean("github_sync_enabled", isChecked)
                apply()
            }
        }
    }
}
