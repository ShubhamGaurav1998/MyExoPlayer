package com.example.videoplayer.room

import androidx.room.*

@Dao
interface RoomDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertVideos(videos: List<VideoListApiEntity>): List<Long>

    @Query("SELECT * FROM videos")
    suspend fun getVideos(): List<VideoListApiEntity>

    @Delete
    suspend fun deleteVideos(video: VideoListApiEntity): Int

//    @Update
//    suspend fun updateVideoInfo(video: VideoListApiEntity)

    @Query("SELECT * FROM videos ORDER BY CASE WHEN :isAsc = 1 THEN timesPlayed END ASC, CASE WHEN :isAsc = 0 THEN timesPlayed END DESC")
    suspend fun arrangeByNoOfTimesPlayed(isAsc: Boolean): List<VideoListApiEntity>

    @Query("SELECT * FROM videos ORDER BY CASE WHEN :isAsc = 1 THEN lastPlayed END ASC, CASE WHEN :isAsc = 0 THEN lastPlayed END DESC")
    suspend fun arrangeByLastPlayed(isAsc: Boolean): List<VideoListApiEntity>

    @Query("DELETE FROM videos")
    suspend fun deleteAllVideos(): Int

    @Query("UPDATE videos SET timesPlayed = :timesPlayed, lastPlayed = :lastPlayed WHERE id = :id")
    suspend fun updateVideoInfoById(timesPlayed: Int, lastPlayed: Long, id: Int)
}