package dev.danielk.gitnote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import dev.danielk.gitnote.R
import dev.danielk.gitnote.model.Note
import io.noties.markwon.Markwon
import io.noties.markwon.image.coil.CoilImagesPlugin

class NoteAdapter(
    private val notes: List<Note>,
    private val onNoteClick: (Note) -> Unit,
    private val onNoteLongClick: (Note) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvContent: TextView = view.findViewById(R.id.tvContent)
        val ivItemImage: ImageView = view.findViewById(R.id.ivItemImage)
        val markwon = Markwon.builder(view.context)
            .usePlugin(CoilImagesPlugin.create(view.context))
            .build()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.tvTitle.text = note.title
        holder.markwon.setMarkdown(holder.tvContent, note.content)
        
        if (note.imageUri != null) {
            holder.ivItemImage.visibility = View.VISIBLE
            holder.ivItemImage.load(note.imageUri)
        } else {
            holder.ivItemImage.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            onNoteClick(note)
        }
        holder.itemView.setOnLongClickListener {
            onNoteLongClick(note)
            true
        }
    }

    override fun getItemCount() = notes.size
}
