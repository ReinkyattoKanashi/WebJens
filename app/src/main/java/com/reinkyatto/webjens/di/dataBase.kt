package com.reinkyatto.webjens.di

import com.reinkyatto.webjens.db.local.DataBase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

val dataBase: Module = module {
    single {
        DataBase.getInstance(androidContext())
    }
}
