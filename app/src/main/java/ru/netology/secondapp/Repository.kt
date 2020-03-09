package ru.netology.secondapp

import android.graphics.Bitmap
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.netology.secondapp.api.*
import ru.netology.secondapp.api.interceptor.InjectAuthTokenInterceptor
import ru.netology.secondapp.dto.AttachmentModel
import java.io.ByteArrayOutputStream

object Repository {

    private var token: String? = null

    private var retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    fun createRetrofitWithAuthToken(authToken: String) {
        val httpLoggerInterceptor = HttpLoggingInterceptor()
        httpLoggerInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(InjectAuthTokenInterceptor(authToken))
            .addInterceptor(httpLoggerInterceptor)
            .build()
        retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        API = retrofit.create(ru.netology.secondapp.api.API::class.java)
    }

    private var API: API =
        retrofit.create(ru.netology.secondapp.api.API::class.java)

    suspend fun authenticate(login: String, password: String) {
        return API.authenticate(AuthRequestParams(login, password)).let {
            if (it.isSuccessful) {
                token = it.body()?.token
            }
        }
    }

    suspend fun register(login: String, password: String) =
        API.register(RegistrationRequestParams(login, password))

    suspend fun createPost(content: String, attachmentModel: AttachmentModel?) = API.createPost(
        CreatePostRequest(content = content, attachmentId = attachmentModel?.id)
    )

    suspend fun getPosts() = API.getPosts()

    suspend fun getRecentPosts() = API.getRecentPosts()

    suspend fun getPostsBefore(id: Int) = API.getPostsBefore(id)

    suspend fun getPostsAfter(id: Int) = API.getPostsAfter(id)

    suspend fun like(id: Int) = API.like(id)

    suspend fun dislike(id: Int) = API.dislike(id)

    suspend fun repost(id: Int, content: String) = API.repost(id, CreateRepostRequest(content = content))

    suspend fun uploadImage(bitmap: Bitmap): Response<AttachmentModel> {
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        val reqFile = RequestBody.create(MediaType.parse("image/jpeg"), bos.toByteArray())
        val body = MultipartBody.Part.createFormData("file", "image.jpg", reqFile)
        return API.uploadImage(body)
    }

    suspend fun registerPushToken(token: String) = API.registerPushToken(this.token!!, PushRequestParams(token))
}