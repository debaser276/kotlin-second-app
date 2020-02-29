package ru.netology.firstapp.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.post_item_view.view.*
import ru.netology.secondapp.*
import ru.netology.secondapp.dto.PostModel
import java.text.SimpleDateFormat
import java.util.*

open class PostViewHolder(adapter: PostAdapter, view: View): RecyclerView.ViewHolder(view) {

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
            commentsIv.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val post = adapter.list[adapterPosition]
                    post.comments++
                    if (!post.commentedByAuthor) post.commentedByAuthor = !post.commentedByAuthor
                    adapter.notifyItemChanged(adapterPosition)
                }
            }

            sharesIv.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val post = adapter.list[adapterPosition]
                    post.shares++
                    if (!post.sharedByAuthor) post.sharedByAuthor = !post.sharedByAuthor
                    adapter.notifyItemChanged(adapterPosition)
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT,
                            "${post.author} ${SimpleDateFormat("dd.MM.yyyy HH:mm")
                                .format(Date(post.created * 1000))}: ${post.content}")
                    }
                    itemView.context.startActivity(intent)
                }
            }
            with(itemView) {
                hideIv.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        adapter.list.removeAt(adapterPosition)
                        adapter.notifyItemRemoved(adapterPosition)
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
                post.comments <= 0 -> commentsTv.visibility = View.GONE
                post.comments in 1..999 -> commentsTv.text = post.comments.toString()
                else -> commentsTv.text = "999+"
            }
            when {
                post.shares <= 0 -> sharesTv.visibility = View.GONE
                post.shares in 1..999 -> sharesTv.text = post.shares.toString()
                else -> sharesTv.text = "999+"
            }

            when {
                post.likeActionPerforming -> {
                    likesTv.setTextColor(Color.BLUE)
                    likesIv.setImageResource(R.drawable.ic_likes_blue_24dp)
                }
                post.likedSet.contains(context.getUserId()) -> {
                    likesTv.setTextColor(Color.RED)
                    likesIv.setImageResource(R.drawable.ic_likes_red_24dp)
                }
                else -> {
                    likesTv.setTextColor(Color.GRAY)
                    likesIv.setImageResource(R.drawable.ic_likes_gray_24dp)
                }
            }
            if (post.commentedByAuthor) {
                commentsTv.setTextColor(Color.RED)
                commentsIv.setImageResource(R.drawable.ic_comments_red_24dp)
            }
            if (post.sharedByAuthor) {
                sharesTv.setTextColor(Color.RED)
                sharesIv.setImageResource(R.drawable.ic_shares_red_24dp)
            }
        }
    }
}