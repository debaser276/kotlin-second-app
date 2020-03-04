package ru.netology.secondapp.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import ru.netology.secondapp.dto.MediaModel
import ru.netology.secondapp.dto.PostModel
import ru.netology.secondapp.dto.PostType

data class AuthRequestParams(val username: String, val password: String)

data class Token(val id: Int, val token: String)

data class RegistrationRequestParams(val username: String, val password: String)

data class CreatePostRequest(val content: String, val type: PostType = PostType.POST, val media: String?)

data class CreateRepostRequest(val content: String, val type: PostType = PostType.REPOST)

interface API {

    @POST("api/v1/authentication")
    suspend fun authenticate(@Body authRequestParams: AuthRequestParams): Response<Token>

    @POST("api/v1/registration")
    suspend fun register(@Body registrationRequestParams: RegistrationRequestParams): Response<Token>

    @POST("/api/v1/posts")
    suspend fun createPost(
        @Body createPostRequest: CreatePostRequest): Response<Void>

    @GET("api/v1/posts")
    suspend fun getPosts(): Response<MutableList<PostModel>>

    @GET("api/v1/posts/recent")
    suspend fun getRecentPosts(): Response<MutableList<PostModel>>

    @GET("/api/v1/posts/{id}/before")
    suspend fun getPostsBefore(@Path("id") id: Int): Response<List<PostModel>>

    @GET("/api/v1/posts/{id}/after")
    suspend fun getPostsAfter(@Path("id") id: Int): Response<List<PostModel>>

    @PUT("api/v1/posts/{id}/like")
    suspend fun like(@Path("id") id: Int): Response<PostModel>

    @PUT("api/v1/posts/{id}/dislike")
    suspend fun dislike(@Path("id") id: Int): Response<PostModel>

    @POST("api/v1/posts/{id}/repost")
    suspend fun repost(
        @Path("id") id: Int,
        @Body createRepostRequest: CreateRepostRequest
    ): Response<PostModel>

    @Multipart
    @POST("api/v1/media")
    suspend fun uploadImage(@Part file: MultipartBody.Part): Response<MediaModel>
}