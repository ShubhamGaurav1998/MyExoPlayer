package com.example.videoplayer.di

import android.content.Context
import com.example.videoplayer.views.MainActivity
import dagger.BindsInstance
import dagger.Component
import dagger.Provides
import javax.inject.Singleton

@Singleton
@Component(modules = [RetrofitInstance::class, VideoDatabase::class])
interface ApplicationComponent {
    fun inject(mainActivity: MainActivity)

    @Component.Factory
    interface  Factory {
        fun create(@BindsInstance context: Context): ApplicationComponent
    }

}