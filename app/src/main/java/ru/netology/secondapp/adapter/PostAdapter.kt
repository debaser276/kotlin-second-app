package ru.netology.secondapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.netology.firstapp.adapter.PostViewHolder
import ru.netology.secondapp.R
import ru.netology.secondapp.dto.PostModel
import ru.netology.secondapp.dto.PostType

class PostAdapter(val list: MutableList<PostModel>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var likeBtnClickListener: OnLikeBtnClickListener? = null
    var repostBtnClickListener: OnRepostBtnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when(viewType) {
            PostType.POST.ordinal -> PostViewHolder(
                this,
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.post_item_view, parent, false)
            )
            else -> RepostViewHolder(
                this,
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.repost_item_view, parent, false)
            )
        }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PostViewHolder -> holder.bind(list[position])
            is RepostViewHolder -> holder.bind(list[position])
        }
    }

    override fun getItemViewType(position: Int): Int = list[position].type.ordinal

    interface OnLikeBtnClickListener {
        fun onLikeBtnClicked(item: PostModel, position: Int)
    }

    interface OnRepostBtnClickListener {
        fun onRepostBtnClicked(item: PostModel, position: Int)
    }
}