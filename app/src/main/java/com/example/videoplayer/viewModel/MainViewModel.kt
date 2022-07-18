package com.example.videoplayer.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videoplayer.repository.VideoRepository
import com.example.videoplayer.room.VideoListApiEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(private val videoRepository: VideoRepository) : ViewModel() {

    fun fetchVideoListFromUrl() {
        viewModelScope.launch(Dispatchers.IO) {
            val noOfRows = videoRepository.getVideosFromUrl()
            if (noOfRows > 0) {
                videoRepository.getVideosFromDB()
            }
        }
    }

    val videos: LiveData<List<VideoListApiEntity>>
        get() = videoRepository.videos

    fun deleteVideo(video: VideoListApiEntity) = viewModelScope.launch(Dispatchers.IO) {
        val noOfRows = videoRepository.deleteVideos(video)
        if (noOfRows > 0) {
            videoRepository.getVideosFromDB()
        }
    }

//    fun updateVideoInfo(video: VideoListApiEntity) = viewModelScope.launch(Dispatchers.IO) {
//        videoRepository.updateVideoInformation(video)
//    }

    fun updateVideoInfoById(timesPlayed: Int, lastPlayed: Long, id: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            videoRepository.updateVideoInformationById(timesPlayed, lastPlayed, id)
        }

    fun getVideosFromDB() = viewModelScope.launch(Dispatchers.IO) {
        videoRepository.getVideosFromDB()
    }

    fun arrangeByNoOfTimesPlayed(isAsc: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        videoRepository.arrangeByNoOfTimesPlayedAsc(isAsc)
    }

    fun arrangeByLastPlayed(isAsc: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        videoRepository.arrangeByLastPlayedAsc(isAsc)
    }

    fun deleteAllVideos() = viewModelScope.launch(Dispatchers.IO) {
        val noOfRows = videoRepository.deleteAllVideos()
        if (noOfRows > 0) {
            videoRepository.getVideosFromDB()
        }
    }

}