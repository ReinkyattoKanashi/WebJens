package com.reinkyatto.webjens.remote.model.server

data class ServerDetailInfo(
    val game_config: GameConfig?,
    val message: String?,
    val server_data: ServerData?,
    val status: Int?
)