package ru.netology.secondapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.netology.secondapp.R
import ru.netology.secondapp.dto.PostModel

class PostAdapter(val list: MutableList<PostModel>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var likeBtnClickListener: OnLikeBtnClickListener? = null
    var repostBtnClickListener: OnRepostBtnClickListener? = null
    var morePostsBtnClickListener: OnMorePostsBtnClickListener? = null

    companion object {
        private const val ITEM_TYPE_POST = 1
        private const val ITEM_TYPE_REPOST = 2
        private const val ITEM_FOOTER = 3
    }

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
            else -> FooterViewHolder(
                this,
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_load_more, parent, false)
            )
        }

    override fun getItemCount(): Int = list.size + 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PostViewHolder -> holder.bind(list[position])
            is RepostViewHolder -> holder.bind(list[position])
        }
    }

    override fun getItemViewType(position: Int): Int =
        when {
            position == list.size -> ITEM_FOOTER
            list[position].sourceId == null -> ITEM_TYPE_POST
            else -> ITEM_TYPE_REPOST
        }

    interface OnLikeBtnClickListener {
        fun onLikeBtnClicked(item: PostModel, position: Int)
    }

    interface OnRepostBtnClickListener {
        fun onRepostBtnClicked(item: PostModel, position: Int)
    }

    interface OnMorePostsBtnClickListener {
        fun onMorePostsBtnClicked(itemView: View, adapter: PostAdapter)
    }
}