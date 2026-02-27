package dev.danielk.gitnote.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val imageUri: String? = null,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable
