package com.example.videoplayer.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.videoplayer.retrofit.VideoService
import com.example.videoplayer.di.VideoDatabase
import com.example.videoplayer.room.VideoListApiEntity
import com.example.videoplayer.utils.Constants
import javax.inject.Inject

class VideoRepository @Inject constructor(
    private val videoService: VideoService,
    private val videoDatabase: VideoDatabase)
{
    private val videosLivedata = MutableLiveData<List<VideoListApiEntity>>()
    val videos: LiveData<List<VideoListApiEntity>>
        get() = videosLivedata

    suspend fun getVideosFromUrl(): Int {
        var noOfRows = 0
            val result =
                videoService.getResults(Constants.VIDEOS_API_PATH, Constants.VIDEOS_API_SUBPATH)
            val listOfVideoListApiEntity = arrayListOf<VideoListApiEntity>()
            if (result.body() != null) {
                result.body()!!.iterator().forEach {
                    val videoListApiEntity =
                        VideoListApiEntity(it.fullName, it.id, it.rating, it.videos_sources, 0, 0)
                    listOfVideoListApiEntity.add(videoListApiEntity)
                }
                noOfRows = videoDatabase.videodao().insertVideos(listOfVideoListApiEntity).size
            }
        return noOfRows
    }

    suspend fun getVideosFromDB() {
        val videos = videoDatabase.videodao().getVideos()
        val videosArrayList = ArrayList<VideoListApiEntity>()
        videosArrayList.addAll(videos)
        videosLivedata.postValue(videosArrayList)
    }

    suspend fun deleteVideos(video: VideoListApiEntity): Int {
        val noOfRows = videoDatabase.videodao().deleteVideos(video)
        return noOfRows
    }

//    suspend fun updateVideoInformation(video: VideoListApiEntity) {
//        videoDatabase.videodao().updateVideoInfo(video)
//    }

    suspend fun updateVideoInformationById(timesPlayed: Int, lastPlayed: Long, id: Int) {
        videoDatabase.videodao().updateVideoInfoById(timesPlayed, lastPlayed, id)
    }

    suspend fun arrangeByNoOfTimesPlayedAsc(isAsc: Boolean) {
        val videos = videoDatabase.videodao().arrangeByNoOfTimesPlayed(isAsc)
        val videosArrayList = ArrayList<VideoListApiEntity>()
        videosArrayList.addAll(videos)
        videosLivedata.postValue(videosArrayList)
    }

    suspend fun arrangeByLastPlayedAsc(isAsc: Boolean) {
        val videos = videoDatabase.videodao().arrangeByLastPlayed(isAsc)
        val videosArrayList = ArrayList<VideoListApiEntity>()
        videosArrayList.addAll(videos)
        videosLivedata.postValue(videosArrayList)
    }

    suspend fun deleteAllVideos(): Int {
        return videoDatabase.videodao().deleteAllVideos()
    }

}