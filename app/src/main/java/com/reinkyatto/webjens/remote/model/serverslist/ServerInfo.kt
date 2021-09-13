package com.reinkyatto.webjens.remote.model.serverslist

import com.reinkyatto.webjens.db.local.tables.serverlist.Server
import com.reinkyatto.webjens.utils.Const

data class ServerInfo(
    val database: String?,
    val game_code: String?,
    val game_donate: String?,
    val game_fps: String?,
    val game_id: String?,
    val game_location: String?,
    val game_max_port: String?,
    val game_max_slots: String?,
    val game_min_port: String?,
    val game_min_slots: String?,
    val game_mysql_free: String?,
    val game_name: String?, // todo Название игры
    val game_private: String?,
    val game_status: String?, // todo Статус
    val game_tariff_testid: String?,
    val game_test_slots: String?,
    val game_tickrate: String?,
    val game_type: String?,
    val location_aboutlocation: String?, // todo Инфа о локации
    val location_alert: String?,
    val location_cost: String?,
    val location_cpus: String?,
    val location_day_off: String?,
    val location_domain: String?,
    val location_fastdl_type: String?,
    val location_id: String?,
    val location_ip: String?,
    val location_ip2: String?,
    val location_locate: String?,
    val location_name: String?,
    val location_os: String?,
    val location_port_ftp: String?,
    val location_ram: String?,
    val location_ssh_port: String?,
    val location_status: String?,
    val location_type: String?,
    val monitoring_hide: String?,
    val monitoring_hostname: String?,
    val monitoring_password: String?,
    val monitoring_players: String?,
    val owner_user_id: String?,
    val ranked_id: String?,
    val server_add_memory: String?,
    val server_affinity: String?,
    val server_autopay: String?,
    val server_blist_url: String?,
    val server_cfg_writer: String?,
    val server_cpu_load: String?,
    val server_data: String?,
    val server_date_end: String?,
    val server_date_reg: String?,
    val server_fastdl: String?,
    val server_fps: String?,
    val server_free: String?,
    val server_ftp: String?,
    val server_gid: String?,
    val server_hdd: String?,
    val server_hdd_load: String?,
    val server_id: String?, // todo SERVER ID
    val server_name: String?,
    val server_own_start: String?,
    val server_password: String?,
    val server_pid: String?,
    val server_port: String?,
    val server_ram_load: String?,
    val server_ramdisk: String?,
    val server_save_status: String?,
    val server_site: String?,
    val server_skype: String?,
    val server_slots: String?,
    val server_stats_url: String?,
    val server_status: String?,
    val server_steamworkshop: String?,
    val server_temp_install: String?,
    val server_tickrate: String?,
    val server_type: String?,
    val server_uid: String?,
    val server_version: String?,
    val server_vk: String?,
    val server_wakeup: String?,
    val tariff_id: String?,
    val user_id: String?
){
    fun toServerDBModel(): Server{
        return Server(
            id = server_id?:"",
            name = game_name?:"",
            game = getGame(game_name?:""),
            ip = location_ip?:"",
            port = server_port?:"",
            status = server_status?.toInt()?:-1
        )
    }

    private fun getGame(name: String): String = when {
        name.indexOf(Const.MINECRAFT) != -1 -> Const.MINECRAFT
        name.indexOf(Const.SAMP) != -1 -> Const.SAMP
        else -> ""
    }
}