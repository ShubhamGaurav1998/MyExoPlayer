package com.example.videoplayer.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.videoplayer.repository.VideoRepository
import javax.inject.Inject

class MainViewModelFactory @Inject constructor(private val videoRepository: VideoRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(videoRepository) as T
    }
}