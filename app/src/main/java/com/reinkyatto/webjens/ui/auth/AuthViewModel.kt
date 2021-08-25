package com.reinkyatto.webjens.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.reinkyatto.webjens.arch.SingleLiveEvent
import com.reinkyatto.webjens.remote.ApiRequests
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

class AuthViewModel(private val api: ApiRequests) : ViewModel() {

    var loginField: String = ""
    var passwordField: String = ""

    private val _navigationEvent = SingleLiveEvent<NavDirections>()
    val navigationEvent: LiveData<NavDirections> = _navigationEvent

    private val _fieldIsEmpty = MutableLiveData("")
    val fieldIsEmpty: LiveData<String> = _fieldIsEmpty

    private val _authSuccess = MutableLiveData("")
    val authSuccess: LiveData<String> = _authSuccess

    private val _authFailed = MutableLiveData("")
    val authFailed: LiveData<String> = _authFailed

    private val _closeKeyboard = MutableLiveData(false)
    val closeKeyboard: LiveData<Boolean> = _closeKeyboard

    private val _noInternetConnection = MutableLiveData("")
    val noInternetConnection: LiveData<String> = _noInternetConnection

    fun onClickLogin() {
        _closeKeyboard.value = true
        if (validateFields()) login() else {
            _fieldIsEmpty.value = "Заполните все поля!"
        }
    }

    private fun login() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = api.auth(loginField, passwordField)
                    .awaitResponse()
                if (response.isSuccessful) {
                    val data = response.body()!!
                    when (data.status) {
                        1 -> _authSuccess.postValue(data.token)
                        0 -> _authFailed.postValue(data.message)
                    }
                }
            } catch (e: Exception) {
                _noInternetConnection.postValue("Походу инет капут")
            }
        }
    }

    private fun validateFields(): Boolean {
        return loginField.isNotEmpty() && passwordField.isNotEmpty()
    }
}