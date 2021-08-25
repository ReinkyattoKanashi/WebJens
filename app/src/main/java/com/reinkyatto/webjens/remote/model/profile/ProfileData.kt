package com.reinkyatto.webjens.remote.model.profile;

import com.google.gson.annotations.SerializedName;

data class ProfileData(
    @SerializedName("status") var status: Int,
    @SerializedName("message") var message: String,
    @SerializedName("server_data") var serverData: ServerData
)

