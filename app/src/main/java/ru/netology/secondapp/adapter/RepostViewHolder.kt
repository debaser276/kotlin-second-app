package ru.netology.secondapp.adapter

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.post_item_view.view.*
import ru.netology.secondapp.R
import ru.netology.secondapp.TimeConverter
import ru.netology.secondapp.dto.PostModel
import ru.netology.secondapp.getUserId
import ru.netology.secondapp.toast

open class RepostViewHolder(adapter: PostAdapter, view: View): RecyclerView.ViewHolder(view) {

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
                else -> likesTv.text = "999+"
            }

            when {
                post.likeActionPerforming -> {
                    likesTv.setTextColor(Color.BLUE)
                    likesIv.setImageResource(R.drawable.ic_likes_pending_24dp)
                }
                post.likedSet.contains(context.getUserId()) -> {
                    likesTv.setTextColor(Color.RED)
                    likesIv.setImageResource(R.drawable.ic_likes_active_24dp)
                }
                else -> {
                    likesTv.setTextColor(Color.GRAY)
                    likesIv.setImageResource(R.drawable.ic_likes_inactive_24dp)
                }
            }
        }
    }
}