package ru.netology.secondapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.IOException

class CreatePostActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private var dialog: LoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        createPostBtn.setOnClickListener {
            launch {
                dialog = LoadingDialog(this@CreatePostActivity).apply {
                    setTitle(R.string.creating_new_post)
                    show()
                }
                try {
                    val result = Repository.createPost(contentEdt.text.toString())
                    if (result.isSuccessful) {
                        toast(R.string.post_created_successfully)
                        finish()
                    } else {
                        toast(R.string.error_occured)
                    }
                } catch (e: IOException) {
                    toast(R.string.error_occured)
                } finally {
                    dialog?.dismiss()
                }
            }
        }
    }
}
