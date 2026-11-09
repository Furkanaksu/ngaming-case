package com.furkanaksu.ngamingcase

import android.app.Application
import com.furkanaksu.ngamingcase.di.initKoin
import org.koin.android.ext.koin.androidContext

class CaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@CaseApplication)
        }
    }
}
