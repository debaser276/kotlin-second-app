package ru.netology.secondapp.api

import retrofit2.Response
import retrofit2.http.*
import ru.netology.secondapp.dto.PostModel
import ru.netology.secondapp.dto.PostType

data class AuthRequestParams(val username: String, val password: String)

data class Token(val id: Int, val token: String)

data class RegistrationRequestParams(val username: String, val password: String)

data class CreatePostRequest(val content: String, val type: PostType = PostType.POST)

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

    @PUT("api/v1/posts/{id}/like")
    suspend fun like(@Path("id") id: Int): Response<PostModel>

    @PUT("api/v1/posts/{id}/dislike")
    suspend fun dislike(@Path("id") id: Int): Response<PostModel>
}