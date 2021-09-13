package com.reinkyatto.webjens.di

import com.reinkyatto.webjens.ui.auth.AuthViewModel
import com.reinkyatto.webjens.ui.server.ServerViewModel
import com.reinkyatto.webjens.ui.serverslist.ServersListViewModel
import com.reinkyatto.webjens.ui.splash.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModel = module {
    viewModel { AuthViewModel() }
    viewModel { ServersListViewModel(get()) }
    viewModel { ServerViewModel(get()) }
}