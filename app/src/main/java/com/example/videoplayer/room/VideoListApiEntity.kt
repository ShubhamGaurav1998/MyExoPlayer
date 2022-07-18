package com.example.videoplayer.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose

@Entity(tableName = "videos")
data class VideoListApiEntity(

    val fullName: String,

    @PrimaryKey
    val id: Int,

    val rating: String,

    val videos_sources: String,

    var timesPlayed: Int,

    var lastPlayed: Long,

    var isPlaying: Boolean = false
)