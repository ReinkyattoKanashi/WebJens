package com.reinkyatto.webjens.remote

import com.reinkyatto.webjens.BuildConfig
import com.reinkyatto.webjens.remote.model.actions.ServerAction
import com.reinkyatto.webjens.remote.model.auth.AuthModel
import com.reinkyatto.webjens.remote.model.console.ConsoleData
import com.reinkyatto.webjens.remote.model.profile.ProfileData
import com.reinkyatto.webjens.remote.model.server.ServerDetailInfo
import com.reinkyatto.webjens.remote.model.serverslist.ServersList
import com.reinkyatto.webjens.utils.Const.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface ApiRequests {

    @GET("./auth")
    fun auth(
        @Query("user_email") email: String,
        @Query("user_password") pass: String
    ): Call<AuthModel>

    @GET("./game_get_all_servers")
    fun getServersList(@Query("token") token: String): Call<ServersList>

    @GET("./game_get_server")
    fun getServer(
        @Query("token") token: String,
        @Query("server_id") serverId: String
    ): Call<ServerDetailInfo>

    @GET("./game_get_console")
    fun getConsole(
        @Query("token") token: String,
        @Query("server_id") serverId: String
    ): Call<ConsoleData>

    @GET("./user_get_data")
    fun getUserData(@Query("token") token: String): Call<ProfileData>

    @GET("./game_send_action")
    fun sendServerAction(
        @Query("token") token: String,
        @Query("server_id") serverId: String,
        @Query("server_action") action: String
    ): Call<ServerAction>


    companion object {
        operator fun invoke(): ApiRequests {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level =
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            val client = OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(ApiRequests::class.java)
        }
    }
}