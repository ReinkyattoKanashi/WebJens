package com.reinkyatto.webjens.remote

import com.reinkyatto.webjens.remote.model.auth.AuthModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiRequests {

    @GET("./auth")
    fun auth(@Query("user_email") email: String, @Query("user_password") pass: String): Call<AuthModel>

}