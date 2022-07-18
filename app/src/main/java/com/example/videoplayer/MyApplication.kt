package com.example.videoplayer

import android.app.Application
import com.example.videoplayer.di.ApplicationComponent
import com.example.videoplayer.di.DaggerApplicationComponent

class MyApplication: Application() {
    lateinit var applicationComponent: ApplicationComponent
    override fun onCreate() {
        super.onCreate()

        applicationComponent = DaggerApplicationComponent.factory().create(this)
    }
}