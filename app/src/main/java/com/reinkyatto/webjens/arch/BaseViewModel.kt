package com.reinkyatto.webjens.arch

import androidx.lifecycle.ViewModel
import com.reinkyatto.webjens.prefs.SharedPrefs
import com.reinkyatto.webjens.remote.ApiRequests

abstract class BaseViewModel : ViewModel() {
    val api by lazy {
        ApiRequests.invoke()
    }
    val sharedPrefs by lazy {
        SharedPrefs()
    }
}