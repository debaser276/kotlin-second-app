package ru.netology.secondapp.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_load_new.view.*

class HeaderViewHolder(val adapter: PostAdapter, view: View) : RecyclerView.ViewHolder(view) {
    init {
        with(itemView) {
            loadNewBtn.setOnClickListener {
                val currentPosition = adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    adapter.newPostsBtnClickListener?.onNewPostsBtnClicked(itemView, adapter)
                }
            }
        }
    }
}