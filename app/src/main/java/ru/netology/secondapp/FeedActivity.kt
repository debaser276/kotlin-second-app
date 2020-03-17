package ru.netology.secondapp

import android.app.Activity
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
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.android.synthetic.main.activity_feed.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_load_more.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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

    companion object {
        private const val PLAY_SERVICES_RESOLUTION_REQUEST = 9000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scheduleJob()
        setContentView(R.layout.activity_feed)

        requestToken()

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
                if (response.isSuccessful) {
                    val newItems = response.body()!!
                    postAdapter.list.addAll(0, newItems)
                    postAdapter.notifyItemRangeInserted(0, newItems.size)
                } else {
                    toast(R.string.error_occured)
                }
            } catch (e: IOException) {
                toast(R.string.error_occured)
            } catch (e: IndexOutOfBoundsException) {
                toast(R.string.no_post)
            } finally {
                swipeContainer.isRefreshing = false
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

    private fun requestToken() {
        with(GoogleApiAvailability.getInstance()) {
            val code = isGooglePlayServicesAvailable(this@FeedActivity)
            if (code == ConnectionResult.SUCCESS) {
                onActivityResult(PLAY_SERVICES_RESOLUTION_REQUEST, Activity.RESULT_OK, null)
            } else if (isUserResolvableError(code)) {
                getErrorDialog(this@FeedActivity, code, PLAY_SERVICES_RESOLUTION_REQUEST).show()
            } else {
                Snackbar.make(root, R.string.google_play_unavailable, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            PLAY_SERVICES_RESOLUTION_REQUEST -> {
                if (resultCode == Activity.RESULT_OK) {
                    launch {
                        val token = FirebaseInstanceId.getInstance().instanceId.await().token
                        Repository.registerPushToken(token)
                    }
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFirstTime(this)) {
            NotificationHelper.comebackNotification(this)
        }
        setLastVisitTime(this, System.currentTimeMillis())
        cancel()
    }

    private fun scheduleJob() {
        val checkWork = PeriodicWorkRequestBuilder<UserNotHereWorker>(
            SHOW_NOTIFICATION_AFTER_UNVISITED_MS, TimeUnit.MILLISECONDS
        ).build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork("uses_present_work", ExistingPeriodicWorkPolicy.KEEP, checkWork)
    }
}
