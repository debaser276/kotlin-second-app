package ru.netology.secondapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.android.synthetic.main.activity_feed.*
import kotlinx.android.synthetic.main.item_load_more.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import ru.netology.secondapp.adapter.PostAdapter
import ru.netology.secondapp.dto.PostModel
import java.io.IOException
import java.util.concurrent.TimeUnit

class FeedActivity : AppCompatActivity(),
    CoroutineScope by MainScope(),
    PostAdapter.OnLikeBtnClickListener,
    PostAdapter.OnRepostBtnClickListener,
    PostAdapter.OnMorePostsBtnClickListener {

    private lateinit var postAdapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scheduleJob()
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
            val dialog = LoadingDialog(this@FeedActivity).apply {
                setTitle(R.string.getting_posts)
                show()
            }
            try {
                val result = Repository.getRecentPosts()
                if (result.isSuccessful) {
                    with(container) {
                        layoutManager = LinearLayoutManager(this@FeedActivity)
                        postAdapter = PostAdapter(result.body() ?: mutableListOf()).apply {
                            likeBtnClickListener = this@FeedActivity
                            repostBtnClickListener = this@FeedActivity
                            morePostsBtnClickListener = this@FeedActivity
                        }
                        adapter = postAdapter
                    }
                } else if (result.code() == 401) {
                    toast(R.string.unauthorized)
                    getSharedPreferences(API_SHARED_FILE, Context.MODE_PRIVATE).edit {
                        clear()
                        apply()
                    }
                    startActivity(Intent(this@FeedActivity, MainActivity::class.java))
                } else {
                    toast(R.string.error_occured)
                }
            } catch(e: IOException) {
                toast(R.string.error_occured)
            } finally {
                dialog.dismiss()
            }
        }
    }

    override fun onLikeBtnClicked(item: PostModel, position: Int) {
        launch {
            item.likeActionPerforming = true
            with (container) {
                try {
                    postAdapter.notifyItemChanged(position)
                    val response = if (item.likedSet.contains(context.getUserId())) {
                        Repository.dislike(item.id)
                    } else {
                        Repository.like(item.id)
                    }
                    if (response.isSuccessful) {
                        item.updateLikes(response.body()!!)
                    }
                    postAdapter.notifyItemChanged(position)
                } catch (e: IOException) {
                    toast(R.string.error_occured)
                } finally {
                    item.likeActionPerforming = false
                    postAdapter.notifyItemChanged(position)
                }
            }
        }
    }

    override fun onRepostBtnClicked(item: PostModel, position: Int) {
        val dialog = AlertDialog.Builder(this@FeedActivity)
            .setView(R.layout.activity_create_post)
            .show()
        dialog.createPostBtn.setOnClickListener {
            launch {
                try {
                    Repository.repost(item.id, dialog.contentEdt.text.toString())
                } catch (e: IOException) {
                    toast(R.string.error_occured)
                } finally {
                    dialog.dismiss()
                }
            }
        }
    }

    private fun refreshData() {
        launch {
            try {
                val response = Repository.getPostsAfter(postAdapter.list[0].id)
                swipeContainer.isRefreshing = false
                if (response.isSuccessful) {
                    val newItems = response.body()!!
                    postAdapter.list.addAll(0, newItems)
                    postAdapter.notifyItemRangeInserted(0, newItems.size)
                } else {
                    toast(R.string.error_occured)
                }
            } catch (e: IOException) {
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
                try {
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
                } catch (e: IOException) {
                    toast(R.string.error_occured)
                } finally {
                    progressbarMore.visibility = View.GONE
                    loadMoreBtn.isEnabled = true
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFirstTime(this)) {
            NotificationHelper.comebackNotification(this)
        }
        setLastVisitTime(this, System.currentTimeMillis())
    }

    private fun scheduleJob() {
        val checkWork = PeriodicWorkRequestBuilder<UserNotHereWorker>(
            SHOW_NOTIFICATION_AFTER_UNVISITED_MS, TimeUnit.MILLISECONDS
        ).build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork("uses_present_work", ExistingPeriodicWorkPolicy.KEEP, checkWork)
    }
}
