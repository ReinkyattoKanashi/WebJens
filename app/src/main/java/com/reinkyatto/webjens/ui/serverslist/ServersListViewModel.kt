package com.reinkyatto.webjens.ui.serverslist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import com.reinkyatto.webjens.arch.SingleLiveEvent

class ServersListViewModel: ViewModel() {


    private val _navigationEvent = SingleLiveEvent<NavDirections>()
    val navigationEvent: LiveData<NavDirections> = _navigationEvent
}