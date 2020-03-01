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
import kotlinx.android.synthetic.main.item_load_new.view.*
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
    private lateinit var postAdapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        fab.setOnClickListener {
            startActivity(Intent(this, CreatePostActivity::class.java))
        }

        swipeContainer.setOnRefreshListener {
            refreshData()
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
                    postAdapter = PostAdapter(result.body() ?: mutableListOf()).apply {
                        likeBtnClickListener = this@FeedActivity
                        repostBtnClickListener = this@FeedActivity
                        newPostsBtnClickListener = this@FeedActivity
                        morePostsBtnClickListener = this@FeedActivity
                    }
                    adapter = postAdapter
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
            loadNewBtn.isEnabled = false
            progressbarNew.visibility = View.VISIBLE
            launch {
                val response = Repository.getPostsAfter(adapter.list[0].id)
                progressbarNew.visibility = View.GONE
                loadNewBtn.isEnabled = true
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

    private fun refreshData() {
        launch {
            val response = Repository.getPostsAfter(postAdapter.list[0].id)
            swipeContainer.isRefreshing = false
            if (response.isSuccessful) {
                val newItems = response.body()!!
                postAdapter.list.addAll(0, newItems)
                postAdapter.notifyItemRangeInserted(0, newItems.size)
            } else {
                toast(R.string.error_occured)
            }
        }
    }

    override fun onMorePostsBtnClicked(
        itemView: View,
        adapter: PostAdapter
    ) {
        with (itemView) {
            loadMoreBtn.isEnabled = false
            progressbarMore.visibility = View.VISIBLE
            launch {
                val response = Repository.getPostsBefore(adapter.list[adapter.list.size - 1].id)
                progressbarMore.visibility = View.GONE
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
