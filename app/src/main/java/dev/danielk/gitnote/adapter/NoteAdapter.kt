package dev.danielk.gitnote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.danielk.gitnote.R
import dev.danielk.gitnote.model.Note
import java.text.SimpleDateFormat
import java.util.*

class NoteAdapter(
    private val notes: List<Note>,
    private val onNoteClick: (Note) -> Unit,
    private val onNoteLongClick: (Note) -> Unit,
    private val onSelectionChanged: (Int) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    private var isSelectionMode = false
    private val selectedItems = mutableSetOf<Note>()

    class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvContent: TextView = view.findViewById(R.id.tvContent)
        val tvTags: TextView = view.findViewById(R.id.tvTags)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val cbSelect: CheckBox = view.findViewById(R.id.cbSelect)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    fun setSelectionMode(enabled: Boolean) {
        if (isSelectionMode != enabled) {
            isSelectionMode = enabled
            if (!enabled) {
                selectedItems.clear()
            }
            notifyDataSetChanged()
            onSelectionChanged(selectedItems.size)
        }
    }

    fun getSelectedItems(): List<Note> {
        return selectedItems.toList()
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.tvTitle.text = if (note.title.isEmpty()) "제목 없음" else note.title
        val contentLines = note.content.lines()
        val previewContent = contentLines.drop(1).filter { it.isNotBlank() }.take(3).joinToString("\n")
        holder.tvContent.text = previewContent
        holder.tvDate.text = dateFormat.format(Date(note.updatedAt))

        if (note.tags.isNotEmpty()) {
            holder.tvTags.visibility = View.VISIBLE
            holder.tvTags.text = note.tags.split(",").joinToString(" #", prefix = "#") { it.trim() }
        } else {
            holder.tvTags.visibility = View.GONE
        }

        holder.cbSelect.visibility = if (isSelectionMode) View.VISIBLE else View.GONE
        holder.cbSelect.isChecked = selectedItems.contains(note)

        holder.cbSelect.setOnClickListener {
            if (holder.cbSelect.isChecked) {
                selectedItems.add(note)
            } else {
                selectedItems.remove(note)
            }
            onSelectionChanged(selectedItems.size)
        }

        holder.itemView.setOnClickListener {
            if (isSelectionMode) {
                holder.cbSelect.isChecked = !holder.cbSelect.isChecked
                if (holder.cbSelect.isChecked) {
                    selectedItems.add(note)
                } else {
                    selectedItems.remove(note)
                }
                onSelectionChanged(selectedItems.size)
            } else {
                onNoteClick(note)
            }
        }
        holder.itemView.setOnLongClickListener {
            if (!isSelectionMode) {
                setSelectionMode(true)
                selectedItems.add(note)
                notifyItemChanged(position)
                onSelectionChanged(selectedItems.size)
                true
            } else {
                onNoteLongClick(note)
                true
            }
        }
    }

    override fun getItemCount() = notes.size
}
