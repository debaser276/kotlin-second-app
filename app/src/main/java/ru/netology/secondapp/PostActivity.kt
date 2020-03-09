package ru.netology.secondapp

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.post_item_view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import ru.netology.secondapp.dto.MediaType
import ru.netology.secondapp.dto.PostModel
import java.io.IOException

class PostActivity : AppCompatActivity(), CoroutineScope by MainScope()  {

    private lateinit var post: PostModel

    companion object {
        private const val INTENT_POST_ID = "intent-post-id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post_item_view)

        val postId = intent.getIntExtra(INTENT_POST_ID, 0)
        launch {
            val dialog = LoadingDialog(this@PostActivity).apply {
                setTitle(R.string.getting_post)
                show()
            }
            try {
                val response = Repository.getById(postId)
                if (response.isSuccessful) {
                    post = response.body()!!
                    updateViews()
                }
            } catch (e : IOException) {
                toast(R.string.error_occured)
            } finally {
                dialog.dismiss()
            }
        }
    }

    private fun updateViews() {
        authorTv.text = post.author
        val millis = System.currentTimeMillis()
        val seconds = millis / 1000
        dateTv.text = TimeConverter.convertSeconds(seconds - post.created, this)
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
            post.likedSet.contains(this.getUserId()) -> {
                likesTv.setTextColor(Color.RED)
                likesIv.setImageResource(R.drawable.ic_likes_active_24dp)
            }
            else -> {
                likesTv.setTextColor(Color.GRAY)
                likesIv.setImageResource(R.drawable.ic_likes_inactive_24dp)
            }
        }

        when {
            post.reposts <= 0 -> repostsTv.visibility = View.GONE
            post.reposts in 1..999 -> repostsTv.text = post.reposts.toString()
            else -> repostsTv.setText(R.string.likes_over_limit)
        }
        if (post.repostedSet.contains(this.getUserId())) {
            repostsTv.setTextColor(Color.RED)
            repostsIv.setImageResource(R.drawable.ic_repost_active_24dp)
        }

        when (post.attachment?.mediaType) {
            MediaType.IMAGE -> loadImage(photoImage, post.attachment!!.url)
        }
    }

    private fun loadImage(photoImage: ImageView, imageUrl: String) {
        Glide.with(photoImage)
            .load(imageUrl)
            .into(photoImage)
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}