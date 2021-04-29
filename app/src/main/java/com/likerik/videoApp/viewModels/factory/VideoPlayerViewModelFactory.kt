package com.likerik.videoApp.viewModels.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.likerik.videoApp.viewModels.VideoPlayerViewModel

class VideoPlayerViewModelFactory(
	private val application: Application,
	private val videoPath: String
) : ViewModelProvider.Factory {
	override fun <T : ViewModel?> create(modelClass: Class<T>): T {
		if (modelClass.isAssignableFrom(VideoPlayerViewModel::class.java)) {
			return VideoPlayerViewModel(application, videoPath) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}
