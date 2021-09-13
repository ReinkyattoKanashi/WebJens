package com.reinkyatto.webjens.utils

enum class ServerAction(val action: String) {
    START("start"),
    STOP("stop"),
    RESTART("restart"),
    REINSTALL("reinstall"),
    BACKUP("backup"),
    KILL("kill")
    // (start, reinstall, backup, restart, stop, kill)
}