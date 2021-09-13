package com.reinkyatto.webjens.remote.model.serverslist

data class ServersList(
    val message: String?,
    val servers: List<ServerInfo>?,
    val status: Int?
)