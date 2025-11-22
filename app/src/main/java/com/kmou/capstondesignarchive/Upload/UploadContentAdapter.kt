package com.kmou.capstondesignarchive.Upload

import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kmou.capstondesignarchive.R

class UploadContentAdapter(private val contentList: MutableList<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_IMAGE = 1
        private const val VIEW_TYPE_TEXT = 2
        private const val VIEW_TYPE_LINK = 3
    }

    override fun getItemViewType(position: Int): Int {
        return when (val item = contentList[position]) {
            is Uri -> VIEW_TYPE_IMAGE
            is String -> {
                if (item.startsWith("http://") || item.startsWith("https://")) {
                    VIEW_TYPE_LINK
                } else {
                    VIEW_TYPE_TEXT
                }
            }
            else -> throw IllegalArgumentException("Invalid type of data at position $position")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_IMAGE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_upload_image, parent, false)
                ImageViewHolder(view)
            }
            VIEW_TYPE_TEXT -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_upload_text, parent, false)
                TextViewHolder(view) { position, text -> contentList[position] = text }
            }
            VIEW_TYPE_LINK -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_upload_link, parent, false)
                LinkViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ImageViewHolder -> holder.bind(contentList[position] as Uri)
            is TextViewHolder -> holder.bind(contentList[position] as String)
            is LinkViewHolder -> holder.bind(contentList[position] as String)
        }
    }

    override fun getItemCount(): Int = contentList.size

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.image_view_content)
        fun bind(uri: Uri) {
            Glide.with(itemView.context).load(uri).into(imageView)
        }
    }

    class TextViewHolder(itemView: View, private val onTextChanged: (position: Int, text: String) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val editText: EditText = itemView.findViewById(R.id.edit_text_content)

        init {
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        onTextChanged(adapterPosition, s.toString())
                    }
                }
            })
        }

        fun bind(text: String) {
            editText.setText(text)
        }
    }

    class LinkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val linkTextView: TextView = itemView.findViewById(R.id.link_view_content)
        fun bind(url: String) {
            linkTextView.text = url
        }
    }
}
