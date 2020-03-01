package ru.netology.secondapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.netology.firstapp.adapter.PostViewHolder
import ru.netology.secondapp.R
import ru.netology.secondapp.dto.PostModel
import ru.netology.secondapp.dto.PostType

class PostAdapter(val list: MutableList<PostModel>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_TYPE_POST = 1
    private val ITEM_TYPE_REPOST = 2
    private val ITEM_FOOTER = 3
    private val ITEM_HEADER = 4

    var likeBtnClickListener: OnLikeBtnClickListener? = null
    var repostBtnClickListener: OnRepostBtnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when(viewType) {
            ITEM_TYPE_POST -> PostViewHolder(
                this,
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.post_item_view, parent, false)
            )
            ITEM_TYPE_REPOST -> RepostViewHolder(
                this,
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.repost_item_view, parent, false)
            )
            ITEM_HEADER -> HeaderViewHolder(
                this,
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_load_new, parent, false)
            )
            else -> FooterViewHolder(
                this,
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_load_more, parent, false)
            )
        }

    override fun getItemCount(): Int = list.size + 2

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PostViewHolder -> holder.bind(list[position - 1])
            is RepostViewHolder -> holder.bind(list[position - 1])
        }
    }

    override fun getItemViewType(position: Int): Int =
        when {
            position == 0 -> ITEM_HEADER
            position == list.size + 1 -> ITEM_FOOTER
            list[position - 1].sourceId == null -> ITEM_TYPE_POST
            else -> ITEM_TYPE_REPOST
        }

    interface OnLikeBtnClickListener {
        fun onLikeBtnClicked(item: PostModel, position: Int)
    }

    interface OnRepostBtnClickListener {
        fun onRepostBtnClicked(item: PostModel, position: Int)
    }
}