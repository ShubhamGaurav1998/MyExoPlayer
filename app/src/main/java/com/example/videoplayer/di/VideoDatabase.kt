package com.example.videoplayer.di

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.videoplayer.room.RoomDao
import com.example.videoplayer.room.VideoListApiEntity
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
@Database(entities = [VideoListApiEntity::class], version = 1)
abstract class VideoDatabase : RoomDatabase() {
    abstract fun videodao(): RoomDao

    companion object {
        private var INSTANCE: VideoDatabase? = null

        @Singleton
        @Provides
        fun getDatabase(context: Context): VideoDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context,
                    VideoDatabase::class.java,
                    "videosDB"
                ).build()
            }
            return INSTANCE!!
        }
    }
}