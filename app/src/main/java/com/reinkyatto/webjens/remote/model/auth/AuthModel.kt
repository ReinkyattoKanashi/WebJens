package com.reinkyatto.webjens.remote.model.auth

import com.google.gson.annotations.SerializedName

data class AuthModel(
    @SerializedName("status") var status: Int,
    @SerializedName("message") var message: String,
    @SerializedName("token") var token: String?
)