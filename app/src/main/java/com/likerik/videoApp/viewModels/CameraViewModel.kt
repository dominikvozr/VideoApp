package com.likerik.videoApp.viewModels

import android.app.Application
import android.content.ContentResolver
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.likerik.videoApp.domain.Video
import com.otaliastudios.cameraview.VideoResult
import com.otaliastudios.cameraview.controls.Mode
import kotlinx.coroutines.launch
import java.io.File

class CameraViewModel(application: Application) : AndroidViewModel(application) {
	val cameraMode = Mode.VIDEO
	var resolver: ContentResolver = application.applicationContext.contentResolver

	var video: Video? = null

	private val _videoResult = MutableLiveData<VideoResult>()
	val videoResult: LiveData<VideoResult>
		get() = _videoResult

	private val _btnBgColor = MutableLiveData<String>()
	val btnBgColor: LiveData<String>
		get() = _btnBgColor

	private val _onTakeVideo = MutableLiveData<Boolean>()
	val onTakeVideo: LiveData<Boolean>
		get() = _onTakeVideo

	private val _onChangeCamera = MutableLiveData<Boolean>()
	val onChangeCamera: LiveData<Boolean>
		get() = _onChangeCamera

	fun navigationCompleted() {
		video = null
		_btnBgColor.value = ""
		_onTakeVideo.value = false
	}

	init {
		_btnBgColor.value = "#A3A3A3"
		_onTakeVideo.value = false
	}

	fun setVideoResult(videoResult: VideoResult) {
		_videoResult.value = videoResult
	}


	fun startTakeVideo() {
		if (_onTakeVideo.value == false) {
			if (isExternalStorageWritable()) {
				writeFile()
				_onTakeVideo.value = true
				_btnBgColor.value = "#FF1818"
			} else {
				return
			}
		} else {
			_onTakeVideo.value = false
			_btnBgColor.value = "#A3A3A3"
		}

	}

	private fun isExternalStorageWritable(): Boolean {
		val state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED == state) {
			return true;
		}
		return false;
	}

	private fun writeFile() {
		viewModelScope.launch {
			// Coroutine that will be canceled when the ViewModel is cleared.
			val videoName = videoRandomName()
			val uri = createNewVideoFile(videoName)
			video = Video(uri!!, File(getRealPathFromUri(uri)), videoName, null, null)
		}
	}

	private fun videoRandomName(): String {
		val alphabet: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
		return "VID_" + List(15) { alphabet.random() }.joinToString("") + ".mp4"
	}

	private fun createNewVideoFile(videoName: String): Uri? {
		val folderMain = "Movies/VideoApp"

		val videoCollection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
		val newVideoDetails = ContentValues().apply {
			put(MediaStore.Video.Media.DISPLAY_NAME, videoName)
			put(MediaStore.Video.Media.RELATIVE_PATH, folderMain)
		}

		return resolver.insert(videoCollection, newVideoDetails)
	}

	private fun getRealPathFromUri(contentUri: Uri?): String? {
		var cursor: Cursor? = null
		return try {
			val proj = arrayOf(MediaStore.Video.Media.DATA)
			cursor = resolver.query(contentUri!!, proj, null, null, null)
			val columnIndex: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
			cursor.moveToFirst()
			cursor.getString(columnIndex)
		} finally {
			cursor?.close()
		}
	}

	fun changeCamera() {
		_onChangeCamera.value = true
	}

	fun cameraChanged() {
		_onChangeCamera.value = false
	}
}
