package ru.netology.secondapp.adapter

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.post_item_view.view.*
import ru.netology.secondapp.*
import ru.netology.secondapp.dto.PostModel

class PostViewHolder(adapter: PostAdapter, view: View): BaseViewHolder(adapter, view) {

    init {
        with(itemView) {
            repostsIv.setOnClickListener {
                val currentPosition = adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    val item = adapter.list[currentPosition]
                    adapter.repostBtnClickListener?.onRepostBtnClicked(item, currentPosition)
                }
            }
        }
    }

    override fun bind(post: PostModel) {
        super.bind(post)
        with(itemView) {
            when {
                post.reposts <= 0 -> repostsTv.visibility = View.GONE
                post.reposts in 1..999 -> repostsTv.text = post.reposts.toString()
                else -> repostsTv.setText(R.string.likes_over_limit)
            }
            if (post.repostedSet.contains(context.getUserId())) {
                repostsTv.setTextColor(Color.RED)
                repostsIv.setImageResource(R.drawable.ic_repost_active_24dp)
            }
        }
    }
}