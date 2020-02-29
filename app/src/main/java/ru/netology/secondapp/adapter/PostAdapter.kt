package ru.netology.firstapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.netology.secondapp.R
import ru.netology.secondapp.dto.PostModel
import ru.netology.secondapp.dto.PostType

class PostAdapter(val list: MutableList<PostModel>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var likeBtnClickListener: OnLikeBtnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item_view, parent, false)
        return PostViewHolder(this, view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        with(holder as PostViewHolder) {
            bind(list[adapterPosition])
        }
    }

    override fun getItemViewType(position: Int): Int = list[position].type.ordinal

    interface OnLikeBtnClickListener {
        fun onLikeBtnClicked(item: PostModel, position: Int)
    }
}