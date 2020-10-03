package com.example.videoApp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ServerSampleViewModel : ViewModel() {

	private val _dataPath = MutableLiveData<String>()
	val dataPath: LiveData<String>
		get() = _dataPath

	private val _hlsPath = MutableLiveData<String>()
	val hlsPath: LiveData<String>
		get() = _hlsPath

	private val _mp4Path = MutableLiveData<String>()
	val mp4Path: LiveData<String>
		get() = _mp4Path

	private val _mp3Path = MutableLiveData<String>()
	val mp3Path: LiveData<String>
		get() = _mp3Path

	// https://s3-us-west-2.amazonaws.com/hls-playground/1200/640x360_1200.m3u8

	init {
		_hlsPath.value = "https://s3-us-west-2.amazonaws.com/hls-playground/1200/640x360_1200.m3u8"
	}

	fun hlsClicked() {
		_dataPath.value = "HLS";
	}

	fun closeClicked() {
		_dataPath.value = "";
	}
}
