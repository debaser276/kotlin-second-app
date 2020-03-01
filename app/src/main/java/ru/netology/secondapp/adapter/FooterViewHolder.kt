package ru.netology.secondapp.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_load_more.view.*

class FooterViewHolder(val adapter: PostAdapter, view: View) : RecyclerView.ViewHolder(view) {
    init {
        with(itemView) {
            loadMoreBtn.setOnClickListener {
                val currentPosition = adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    adapter.morePostsBtnClickListener?.onMorePostsBtnClicked(itemView, adapter)
                }
            }
        }
    }
}