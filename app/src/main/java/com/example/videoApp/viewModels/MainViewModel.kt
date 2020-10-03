package com.example.videoApp.viewModels

import android.app.Application
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.videoApp.domain.Video
import java.io.File

private const val LIMIT_VIDEOS = 3

class MainViewModel(application: Application) : AndroidViewModel(application) {
	//private val viewModelJob = Job()
	//private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
	private val resolver = application.contentResolver

	var videos = mutableListOf<Video>()

	private val _gotVideos = MutableLiveData<Boolean>()
	val gotVideos: LiveData<Boolean>
		get() = _gotVideos

	private val _lastVideoId = MutableLiveData<Long>()
	val lastVideoId: LiveData<Long>
		get() = _lastVideoId

	private val _videoCounter = MutableLiveData<Int>()
	val videoCounter: LiveData<Int>
		get() = _videoCounter

	init {
		initializeVideoFiles()
	}

	private fun initializeVideoFiles() {
		//uiScope.launch {
			_gotVideos.value = false
			getVideoFiles()?.let {
				videos = it
			}
			_gotVideos.value = true
		//}
	}

	private /*suspend*/ fun getVideoFiles() : MutableList<Video>? {
		//return withContext(Dispatchers.IO) {

			val projection = arrayOf(
				MediaStore.Video.Media._ID,
				MediaStore.Video.Media.DISPLAY_NAME,
				MediaStore.Video.Media.DURATION,
				MediaStore.Video.Media.SIZE,
				MediaStore.Video.Media.RELATIVE_PATH
			)
			val list = mutableListOf<Video>()

			// Display videos in alphabetical order based on their display name.
			val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

			val query = resolver.query(
				MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
				projection,
				null,
				null,
				sortOrder
			)

			query?.use { cursor ->
				// Cache column indices.
				val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
				val nameColumn =
					cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
				val durationColumn =
					cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
				val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
				val relativePath = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.RELATIVE_PATH)

				while (cursor.moveToNext()) {
					// Get values of columns for a given video.
					val id = cursor.getLong(idColumn)
					val name = cursor.getString(nameColumn)
					val duration = cursor.getInt(durationColumn)
					val size = cursor.getInt(sizeColumn)
					val relativePath = cursor.getString(relativePath)

					val contentUri: Uri = ContentUris.withAppendedId(
						MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
						id
					)

					if (relativePath.toString() == "Movies/VideoApp/") {

						// Stores column values and the contentUri in a local object
						// that represents the media file.
						list += Video(contentUri, File(getRealPathFromUri(contentUri)), name, duration, size)
					}

				}
			}
		return list
		//}
	}

	/*
	* changes content Uri (content://...) to real part Uri (file://...)
	* */
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
}
