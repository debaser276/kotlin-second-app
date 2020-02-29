package ru.netology.secondapp

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.netology.secondapp.api.API
import ru.netology.secondapp.api.AuthRequestParams
import ru.netology.secondapp.api.CreatePostRequest
import ru.netology.secondapp.api.RegistrationRequestParams
import ru.netology.secondapp.api.interceptor.InjectAuthTokenInterceptor

object Repository {

    private var retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://debaser-kotlin-ktor.herokuapp.com/")
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
            .baseUrl("https://debaser-kotlin-ktor.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        API = retrofit.create(ru.netology.secondapp.api.API::class.java)
    }

    private var API: API =
        retrofit.create(ru.netology.secondapp.api.API::class.java)

    suspend fun authenticate(login: String, password: String) =
        API.authenticate(AuthRequestParams(login, password))

    suspend fun register(login: String, password: String) =
        API.register(RegistrationRequestParams(login, password))

    suspend fun createPost(content: String) = API.createPost(
        CreatePostRequest(content = content)
    )

    suspend fun getPosts() = API.getPosts()

    suspend fun like(id: Int) = API.like(id)

    suspend fun dislike(id: Int) = API.dislike(id)
}