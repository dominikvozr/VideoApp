package com.example.videoApp.viewModels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class Mp4VideoPlayerViewModel(application: Application) : AndroidViewModel(application) {

	val context: Context = getApplication<Application>().applicationContext

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

	private val _hlsText = MutableLiveData<String>()
	val hlsText: LiveData<String>
		get() = _hlsText
	init {
		_hlsText.value = ""
		_hlsPath.value = "https://apidev2.opinyour.com/merged/5674894D-5A98-4C46-A541-E6CE666382C5/2324E9A4-65F2-4E8C-8441-17F1A85D00F5/merged.m3u8"
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

	fun postHlsText(text: String) {
		_hlsText.postValue(text)
	}
}
