package ru.netology.secondapp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.netology.secondapp.api.API
import ru.netology.secondapp.api.AuthRequestParams
import ru.netology.secondapp.api.RegistrationRequestParams

object Repository {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://debaser-kotlin-ktor.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val API by lazy {
        retrofit.create(ru.netology.secondapp.api.API::class.java)
    }

    suspend fun authenticate(login: String, password: String) =
        API.authenticate(AuthRequestParams(login, password))

    suspend fun register(login: String, password: String) =
        API.register(RegistrationRequestParams(login, password))
}