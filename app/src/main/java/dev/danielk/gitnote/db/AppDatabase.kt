package dev.danielk.gitnote.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.danielk.gitnote.model.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Note::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gitnote_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Insert dummy data
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                val noteDao = database.noteDao()
                                val sampleNotes = listOf(
                                    Note(title = "Welcome to GitNote", content = "Welcome to GitNote! This is a simple note-taking app that supports Markdown and GitHub synchronization."),
                                    Note(title = "Markdown Support", content = "# Headers\n## Sub-header\n### H3\n\n**Bold text** and *italic text*.\n\n- List item 1\n- List item 2"),
                                    Note(title = "Task List", content = "- [ ] Finish the project\n- [x] Create database\n- [ ] Add sync feature"),
                                    Note(title = "Meeting Notes", content = "## Weekly Sync\n\n- Discussed new features\n- Review PRs\n- Plan for next sprint"),
                                    Note(title = "Grocery List", content = "- Milk\n- Eggs\n- Bread\n- Coffee"),
                                    Note(title = "Code Snippet", content = "```kotlin\nfun main() {\n    println(\"Hello, World!\")\n}\n```"),
                                    Note(title = "Project Ideas", content = "1. AI integration\n2. Voice notes\n3. Cloud backup"),
                                    Note(title = "Book Recommendations", content = "- Clean Code\n- The Pragmatic Programmer\n- Introduction to Algorithms"),
                                    Note(title = "Travel Plans", content = "### Japan Trip\n- Tokyo\n- Kyoto\n- Osaka"),
                                    Note(title = "Daily Journal", content = "Today was a productive day. I implemented the database and added sample data."),
                                    Note(title = "Recipes", content = "## Pancakes\n\n1. Mix flour, milk, eggs\n2. Cook on pan\n3. Serve with syrup"),
                                    Note(title = "Contact Info", content = "Email: developer@example.com\nWebsite: https://example.com")
                                )
                                noteDao.insertAll(sampleNotes)
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
