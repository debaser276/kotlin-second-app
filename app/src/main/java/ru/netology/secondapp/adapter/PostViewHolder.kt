package ru.netology.secondapp.adapter

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.post_item_view.view.*
import ru.netology.secondapp.*
import ru.netology.secondapp.dto.PostModel

class PostViewHolder(adapter: PostAdapter, view: View): RecyclerView.ViewHolder(view) {

    init {
        with(itemView) {
            likesIv.setOnClickListener {
                val currentPosition = adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    val item = adapter.list[currentPosition]
                    if (item.likeActionPerforming) {
                        context.toast(R.string.like_in_progress)
                    } else {
                        adapter.likeBtnClickListener?.onLikeBtnClicked(item, currentPosition)
                    }
                }
            }
            repostsIv.setOnClickListener {
                val currentPosition = adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    val item = adapter.list[currentPosition]
                    adapter.repostBtnClickListener?.onRepostBtnClicked(item, currentPosition)
                }
            }
        }
    }

    fun bind(post: PostModel) {
        with(itemView) {
            authorTv.text = post.author
            val millis = System.currentTimeMillis()
            val seconds = millis / 1000
            dateTv.text = TimeConverter.convertSeconds(seconds - post.created)
            contentTv.text = post.content

            when {
                post.likes <= 0 -> likesTv.visibility = View.GONE
                post.likes in 1..999 -> {
                    if (likesTv.visibility == View.GONE) likesTv.visibility = View.VISIBLE
                    likesTv.text = post.likes.toString()
                }
                else -> likesTv.setText(R.string.likes_over_limit)
            }
            when {
                post.reposts <= 0 -> repostsTv.visibility = View.GONE
                post.reposts in 1..999 -> repostsTv.text = post.reposts.toString()
                else -> repostsTv.setText(R.string.likes_over_limit)
            }

            val userId = context.getUserId()
            when {
                post.likeActionPerforming -> {
                    likesTv.setTextColor(Color.BLUE)
                    likesIv.setImageResource(R.drawable.ic_likes_pending_24dp)
                }
                post.likedSet.contains(userId) -> {
                    likesTv.setTextColor(Color.RED)
                    likesIv.setImageResource(R.drawable.ic_likes_active_24dp)
                }
                else -> {
                    likesTv.setTextColor(Color.GRAY)
                    likesIv.setImageResource(R.drawable.ic_likes_inactive_24dp)
                }
            }
            if (post.repostedSet.contains(userId)) {
                repostsTv.setTextColor(Color.RED)
                repostsIv.setImageResource(R.drawable.ic_repost_active_24dp)
            }
        }
    }
}