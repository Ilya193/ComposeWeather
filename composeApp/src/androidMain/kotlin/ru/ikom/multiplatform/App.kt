package ru.ikom.multiplatform

import android.app.Application
import di.commonAppModule
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(commonAppModule)
        }
    }
}