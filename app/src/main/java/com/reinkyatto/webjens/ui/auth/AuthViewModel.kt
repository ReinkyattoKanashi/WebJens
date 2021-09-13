package com.reinkyatto.webjens.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.reinkyatto.webjens.arch.BaseViewModel
import com.reinkyatto.webjens.arch.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

class AuthViewModel() : BaseViewModel() {

    var loginField: String = ""
    var passwordField: String = ""

    private val _navigationEvent = SingleLiveEvent<NavDirections>()
    val navigationEvent: LiveData<NavDirections> = _navigationEvent

    private val _fieldIsEmpty = MutableLiveData<String>()
    val fieldIsEmpty: LiveData<String> = _fieldIsEmpty

    private val _authSuccess = MutableLiveData<Boolean>(false)
    val authSuccess: LiveData<Boolean> = _authSuccess

    private val _authFailed = MutableLiveData<String>()
    val authFailed: LiveData<String> = _authFailed

    private val _closeKeyboard = MutableLiveData<Boolean>()
    val closeKeyboard: LiveData<Boolean> = _closeKeyboard

    private val _blockLogBtnAndShowProgress = MutableLiveData<Boolean>(false)
    val blockLogBtnAndShowProgress: LiveData<Boolean> = _blockLogBtnAndShowProgress

    private val _noInternetConnection = MutableLiveData<String>()
    val noInternetConnection: LiveData<String> = _noInternetConnection


    private val _responseIsNotSuccess = MutableLiveData<Boolean>(false)
    val responseIsNotSuccess: LiveData<Boolean> = _responseIsNotSuccess


    fun onClickLogin() {
        _closeKeyboard.value = true
        if (isValidFields()) {
            _blockLogBtnAndShowProgress.value = true
            login()
        } else {
            _fieldIsEmpty.value = "Заполните все поля!"
        }
    }

    private fun login() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = api.auth(loginField, passwordField)
                .awaitResponse()
            if (response.isSuccessful) {
                val data = response.body()
                if (data?.status == 1) {
                    val token = data.token
                    if (!token.isNullOrEmpty()) {
                        sharedPrefs.saveToken(token)
                        Log.i("token", token)
                        _authSuccess.postValue(true)
                    } else {
                        _authFailed.postValue("Token is null or empty")
                    }
                } else if (data?.captchaUrl.isNullOrEmpty()) {
                    _authFailed.postValue("Неверный логин или пароль")
                } else {
                    _authFailed.postValue("Проблема с сервером. Попробуйте позже. (captcha)")
                }
            } else {
                _authFailed.postValue("Проблема с сервером. Попробуйте позже. (ddos protect)")
                _responseIsNotSuccess.postValue(true)
            }
            _blockLogBtnAndShowProgress.postValue(false)
        }
    }

    private fun isValidFields(): Boolean {
        return loginField.isNotEmpty() && passwordField.isNotEmpty()
    }
}