package com.reinkyatto.webjens.remote.model.console

import com.google.gson.annotations.SerializedName

data class ConsoleData(
    @SerializedName("status") var status: Int?,
    @SerializedName("message") var message: String?
)