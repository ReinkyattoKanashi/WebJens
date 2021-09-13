package com.reinkyatto.webjens.db.local.tables.serverlist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "servers_list")
data class Server(
    @PrimaryKey(autoGenerate = true)
    val key: Long = 0L,
    val id: String = "",
    val name: String = "",
    var game: String = "",
    val ip: String = "",
    val port: String = "",
    var status: Int = 0
){
    fun toDB(): Server = Server(key, id, name, game, ip, port, -21)
}