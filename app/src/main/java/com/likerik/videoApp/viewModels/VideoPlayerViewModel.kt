package com.likerik.videoApp.viewModels

import android.app.Application
import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.likerik.videoApp.domain.StoriesVideo
import kotlinx.coroutines.*

class VideoPlayerViewModel(
	application: Application,
	val videoPath: String
) : AndroidViewModel(application) {

	private val job = Job()
	private val uiScope = CoroutineScope(Dispatchers.Main + job)

	var resolver: ContentResolver = application.applicationContext.contentResolver
	var video: StoriesVideo? = null

	private var videoName: String = ""
	private var videoId: Long? = null

	private val _onSaveVideo = MutableLiveData<Boolean>()
	val onSaveVideo : LiveData<Boolean>
	get() = _onSaveVideo

	private val _onDeleteVideo = MutableLiveData<Boolean>()
	val onDeleteVideo : LiveData<Boolean>
		get() = _onDeleteVideo

	init {
		initializePathName()
		getId()
		_onDeleteVideo.value = false
		_onSaveVideo.value = false
	}

	fun navigationCompleted() {
		_onDeleteVideo.value = false
		_onDeleteVideo.value = false
		videoName = ""
		videoId = null
		video = null
	}

	private fun initializePathName() {
		val parts = videoPath.split("/")
		videoName = parts[parts.size-1]
	}

	private fun getId() {
		uiScope.launch {
		 videoId = withContext(Dispatchers.IO) {
			val projection = arrayOf(
				MediaStore.Video.Media._ID
			)

			val selection = "${MediaStore.Video.Media.DISPLAY_NAME} = ?"
			val selectionArgs = arrayOf(
				videoName
			)

			val query = resolver.query(
				MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
				projection,
				selection,
				selectionArgs,
				null
			)
			query?.use { cursor ->
				// Cache column indices.
				val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
				var id : Long? = null
				while (cursor.moveToNext()) {
					// Get values of columns for a given video.
					id = cursor.getLong(idColumn)
				}
				id
			}
		 }
		}
	}

	fun saveVideo() {
		_onSaveVideo.value = true
	}

	fun deleteVideo() {
		// URI of the video to remove.
		val contentUri: Uri = ContentUris.withAppendedId(
			MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
			videoId!!
		)

		// When performing a single item delete, prefer using the ID
		val selection = "${MediaStore.Audio.Media._ID} = ?"

		// By using selection + args we protect against improper escaping of // values.
		val selectionArgs = arrayOf(videoId.toString())

		// Perform the actual removal.
		resolver.delete(
			contentUri,
			selection,
			selectionArgs)
		_onDeleteVideo.value = true
	}
}
