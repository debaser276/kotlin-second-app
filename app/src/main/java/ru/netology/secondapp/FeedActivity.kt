package ru.netology.secondapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.android.synthetic.main.activity_feed.*
import kotlinx.android.synthetic.main.item_load_more.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import ru.netology.secondapp.adapter.PostAdapter
import ru.netology.secondapp.dto.PostModel

class FeedActivity : AppCompatActivity(),
    CoroutineScope by MainScope(),
    PostAdapter.OnLikeBtnClickListener,
    PostAdapter.OnRepostBtnClickListener,
    PostAdapter.OnNewPostsBtnClickListener,
    PostAdapter.OnMorePostsBtnClickListener {

    var dialog: LoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        fab.setOnClickListener {
            startActivity(Intent(this, CreatePostActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        launch {
            dialog = LoadingDialog(this@FeedActivity).apply {
                setTitle(R.string.getting_posts)
                show()
            }
            val result = Repository.getRecentPosts()
            dialog?.dismiss()
            if (result.isSuccessful) {
                with(container) {
                    layoutManager = LinearLayoutManager(this@FeedActivity)
                    adapter = PostAdapter(result.body() ?: mutableListOf()).apply {
                        likeBtnClickListener = this@FeedActivity
                        repostBtnClickListener = this@FeedActivity
                        newPostsBtnClickListener = this@FeedActivity
                        morePostsBtnClickListener = this@FeedActivity
                    }
                }
            } else {
                toast(R.string.error_occured)
            }
        }
    }

    override fun onLikeBtnClicked(item: PostModel, position: Int) {
        launch {
            item.likeActionPerforming = true
            with (container) {
                adapter?.notifyItemChanged(position)
                val response = if (item.likedSet.contains(context.getUserId())) {
                    Repository.dislike(item.id)
                } else {
                    Repository.like(item.id)
                }
                item.likeActionPerforming = false
                if (response.isSuccessful) {
                    item.updateLikes(response.body()!!)
                }
                adapter?.notifyItemChanged(position)
            }
        }
    }

    override fun onRepostBtnClicked(item: PostModel, position: Int) {
        val dialog = AlertDialog.Builder(this@FeedActivity)
            .setView(R.layout.activity_create_post)
            .show()
        dialog.createPostBtn.setOnClickListener {
            launch {
                Repository.repost(item.id, dialog.contentEdt.text.toString())
                dialog.dismiss()
            }
        }
    }

    override fun onNewPostsBtnClicked(
        itemView: View,
        adapter: PostAdapter
    ) {
        with (itemView) {
            loadMoreBtn.isEnabled = false
            progressbar.visibility = View.VISIBLE
            launch {
                val response = Repository.getPostsAfter(adapter.list[0].id)
                progressbar.visibility = View.GONE
                loadMoreBtn.isEnabled = true
                if (response.isSuccessful) {
                    val newItems = response.body()!!
                    adapter.list.addAll(0, newItems)
                    adapter.notifyItemRangeInserted(0, newItems.size)
                } else {
                    toast(R.string.error_occured)
                }
            }
        }
    }

    override fun onMorePostsBtnClicked(
        itemView: View,
        adapter: PostAdapter
    ) {
        with (itemView) {
            loadMoreBtn.isEnabled = false
            progressbar.visibility = View.VISIBLE
            launch {
                val response = Repository.getPostsBefore(adapter.list[adapter.list.size - 1].id)
                progressbar.visibility = View.GONE
                loadMoreBtn.isEnabled = true
                if (response.isSuccessful) {
                    val newItems = response.body()!!
                    adapter.list.addAll(newItems)
                    adapter.notifyItemRangeInserted(adapter.list.size, newItems.size)
                } else {
                    toast(R.string.error_occured)
                }
            }
        }
    }
}
