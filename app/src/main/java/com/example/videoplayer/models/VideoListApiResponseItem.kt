package com.example.videoplayer.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose

data class VideoListApiResponseItem(
    val fullName: String,
    val id: Int,
    val rating: String,
    val videos_sources: String,

    @Expose(serialize = false, deserialize = false)
    var timesPlayed: Int = 0,

    @Expose(serialize = false, deserialize = false)
    var lastPlayed: Long
)