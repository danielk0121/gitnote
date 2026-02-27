package dev.danielk.gitnote

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.switchmaterial.SwitchMaterial
import org.json.JSONArray
import org.json.JSONObject

class SettingsActivity : AppCompatActivity() {

    private lateinit var switchGitHubSync: SwitchMaterial
    private lateinit var radioGroupTheme: RadioGroup
    private lateinit var radioSystemDefault: RadioButton
    private lateinit var radioLight: RadioButton
    private lateinit var radioDark: RadioButton
    private lateinit var layoutRepoSettings: LinearLayout
    private lateinit var tvCurrentRepo: TextView
    private lateinit var btnAddRepo: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        switchGitHubSync = findViewById(R.id.switchGitHubSync)
        radioGroupTheme = findViewById(R.id.radioGroupTheme)
        radioSystemDefault = findViewById(R.id.radioSystemDefault)
        radioLight = findViewById(R.id.radioLight)
        radioDark = findViewById(R.id.radioDark)
        layoutRepoSettings = findViewById(R.id.layoutRepoSettings)
        tvCurrentRepo = findViewById(R.id.tvCurrentRepo)
        btnAddRepo = findViewById(R.id.btnAddRepo)

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
        
        // Load GitHub Sync setting
        val sharedPref = getSharedPreferences("gitnote_prefs", MODE_PRIVATE)
        val isSyncEnabled = sharedPref.getBoolean("github_sync_enabled", false)
        switchGitHubSync.isChecked = isSyncEnabled
        layoutRepoSettings.visibility = if (isSyncEnabled) View.VISIBLE else View.GONE
        
        updateCurrentRepoUI()
    }

    private fun updateCurrentRepoUI() {
        val sharedPref = getSharedPreferences("gitnote_prefs", MODE_PRIVATE)
        val currentRepo = sharedPref.getString("current_repo_name", "None selected")
        tvCurrentRepo.text = currentRepo
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
            layoutRepoSettings.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        btnAddRepo.setOnClickListener {
            showManageRepoDialog()
        }
    }

    private fun showManageRepoDialog() {
        val sharedPref = getSharedPreferences("gitnote_prefs", MODE_PRIVATE)
        val repoListJson = sharedPref.getString("repo_list", "[]")
        val repoArray = JSONArray(repoListJson)
        val repoNames = mutableListOf<String>()
        for (i in 0 until repoArray.length()) {
            repoNames.add(repoArray.getJSONObject(i).getString("name"))
        }
        repoNames.add("+ Add New Repository")

        AlertDialog.Builder(this)
            .setTitle("Select or Add Repository")
            .setItems(repoNames.toTypedArray()) { _, which ->
                if (which == repoNames.size - 1) {
                    showAddRepoDialog()
                } else {
                    val selectedRepo = repoArray.getJSONObject(which)
                    with(sharedPref.edit()) {
                        putString("current_repo_name", selectedRepo.getString("name"))
                        putString("current_repo_url", selectedRepo.getString("url"))
                        apply()
                    }
                    updateCurrentRepoUI()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAddRepoDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_repo, null)
        val etRepoName = dialogView.findViewById<EditText>(R.id.etRepoName)
        val etRepoUrl = dialogView.findViewById<EditText>(R.id.etRepoUrl)

        AlertDialog.Builder(this)
            .setTitle("Add Repository")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = etRepoName.text.toString().trim()
                val url = etRepoUrl.text.toString().trim()
                if (name.isNotEmpty() && url.isNotEmpty()) {
                    addNewRepository(name, url)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addNewRepository(name: String, url: String) {
        val sharedPref = getSharedPreferences("gitnote_prefs", MODE_PRIVATE)
        val repoListJson = sharedPref.getString("repo_list", "[]")
        val repoArray = JSONArray(repoListJson)
        
        val newRepo = JSONObject().apply {
            put("name", name)
            put("url", url)
        }
        repoArray.put(newRepo)
        
        with(sharedPref.edit()) {
            putString("repo_list", repoArray.toString())
            putString("current_repo_name", name)
            putString("current_repo_url", url)
            apply()
        }
        updateCurrentRepoUI()
    }
}
