package com.reinkyatto.webjens.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.reinkyatto.webjens.remote.ApiRequests

class AuthViewModelFactory(
    private val api: ApiRequests,
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(api) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}