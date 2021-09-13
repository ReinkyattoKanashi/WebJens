package com.reinkyatto.webjens

import android.app.Application
import android.content.Context
import com.reinkyatto.webjens.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(appModule)
        }
    }

    companion object {
        private var instance: App? = null
        val applicationContext: Context
            get() = instance!!.applicationContext
    }

    init {
        instance = this
    }
}