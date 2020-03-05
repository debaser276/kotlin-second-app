package ru.netology.secondapp.adapter

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.post_item_view.view.*
import ru.netology.secondapp.R
import ru.netology.secondapp.TimeConverter
import ru.netology.secondapp.dto.MediaType
import ru.netology.secondapp.dto.PostModel
import ru.netology.secondapp.getUserId
import ru.netology.secondapp.toast

open class BaseViewHolder(adapter: PostAdapter, view: View) : RecyclerView.ViewHolder(view) {

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

    open fun bind(post: PostModel) {
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

            when (post.attachment?.mediaType) {
                MediaType.IMAGE -> loadImage(photoImage, post.attachment.url)
                else -> {}
            }
        }
    }

    private fun loadImage(photoImage: ImageView, imageUrl: String) {
        Glide.with(photoImage)
            .load(imageUrl)
            .into(photoImage)
    }
}