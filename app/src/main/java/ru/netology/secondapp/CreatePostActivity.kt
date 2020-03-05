package ru.netology.secondapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import ru.netology.secondapp.dto.AttachmentModel
import ru.netology.secondapp.dto.MediaType
import java.io.IOException

class CreatePostActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private var attachmentModel: AttachmentModel? = null

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        createPostBtn.setOnClickListener {
            launch {
                val dialog = LoadingDialog(this@CreatePostActivity).apply {
                    setTitle(R.string.creating_new_post)
                    show()
                }
                try {
                    val result = Repository.createPost(contentEdt.text.toString(), attachmentModel)
                    if (result.isSuccessful) {
                        toast(R.string.post_created_successfully)
                        finish()
                    } else {
                        toast(R.string.error_occured)
                    }
                } catch (e: IOException) {
                    toast(R.string.error_occured)
                } finally {
                    dialog.dismiss()
                }
            }
        }

        attachPhotoImg.setOnClickListener {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(packageManager)?.also {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imageBitmap.let {
                launch {
                    val dialog = LoadingDialog(this@CreatePostActivity).apply {
                        setTitle(R.string.creating_new_post)
                        show()
                    }
                    try {
                        val imageUploadResult = Repository.uploadImage(it)
                        NotificationHelper.mediaUploaded(MediaType.IMAGE, this@CreatePostActivity)
                        if (imageUploadResult.isSuccessful) {
                            attachPhotoImg.setImageResource(R.drawable.ic_add_a_photo_inactive)
                            attachPhotoDoneImg.visibility = View.VISIBLE
                            attachmentModel = imageUploadResult.body()
                        } else {
                            toast(R.string.cant_upload_image)
                        }
                    } catch (e: IOException) {
                        toast(R.string.error_occured)
                    } finally {
                        dialog.dismiss()
                    }
                }
            }
        }
    }
}
