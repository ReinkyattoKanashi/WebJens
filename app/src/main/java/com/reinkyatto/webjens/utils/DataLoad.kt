package com.reinkyatto.webjens.utils

enum class DataLoad(val status: Int) {
    FAILED(0), SUCCESS(1), REFRESH(2), LONG_REFRESH(3), NULL(-1)
}