package com.reinkyatto.webjens.ui.splash

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.reinkyatto.webjens.arch.BaseViewModel
import com.reinkyatto.webjens.arch.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.module.Module
import retrofit2.awaitResponse

class SplashViewModel() : BaseViewModel() {
    private val _navigationEvent = SingleLiveEvent<NavDirections>()
    val navigationEvent: LiveData<NavDirections> = _navigationEvent

    private val _authStatus = MutableLiveData<Int>()
    val authStatus: LiveData<Int> = _authStatus

    private val _noAnswer = MutableLiveData<Boolean>()
    val noAnswer: LiveData<Boolean> = _noAnswer

    var counterRequests: Int = 0

    fun checkToken() {
        val token = sharedPrefs.getToken()
        if (token.isEmpty()) {
            _authStatus.value = -1
        } else {
            isValidToken(token)
        }
    }

    // todo saveToDb data from api for user

    private fun isValidToken(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = api.getUserData(token)
                .awaitResponse()
            Log.i("Retrofit", "Splash::Response() -> ${response.isSuccessful}")
            if (response.isSuccessful) {
                val data = response.body()
                Log.i("Retrofit", "Splash::Data.status -> ${data?.status}")
                if (data?.status == 1) {
                    if (!data.serverData?.userId.isNullOrBlank()) {
                        _authStatus.postValue(1)
                        Log.i("Retrofit", "Splash::Success")
                    } else {
                        _authStatus.postValue(0)
                        Log.i("Retrofit", "Splash::Fail - profile is empty")
                    }
                } else {
                    _authStatus.postValue(0)
                    Log.i("Retrofit", "Splash::Fail - status = 0")
                }
            } else {
                // try again. next time
                counterRequests += 1 // if counter requests == 3, we will destroy token and send on login window
                if (counterRequests != 3) {
                    _noAnswer.postValue(true) // need to new request
                    Log.i("Retrofit", "Splash::Fail - response not success")
                } else _authStatus.postValue(0)
            }
        }
    }
}