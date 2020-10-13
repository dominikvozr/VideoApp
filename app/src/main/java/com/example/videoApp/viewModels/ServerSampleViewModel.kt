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

	init {
		_hlsPath.value = "https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8"
		_mp4Path.value = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
		_mp3Path.value = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
	}

	fun hlsClicked() {
		_dataPath.value = "HLS";
	}

	fun mp4Clicked() {
		_dataPath.value = "MP4";
	}

	fun mp3Clicked() {
		_dataPath.value = "MP3";
	}

	fun closeClicked() {
		_dataPath.value = "";
	}
}
