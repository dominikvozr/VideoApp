package com.likerik.videoApp.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.likerik.videoApp.domain.*
import com.likerik.videoApp.network.NetworkApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class StoriesViewModel : ViewModel() {
	private val _newStoriesVideo = MutableLiveData<MutableList<NewStoriesVideo>>()
	val newStoriesVideo: LiveData<MutableList<NewStoriesVideo>>
		get() = _newStoriesVideo

	init {
		_newStoriesVideo.value = mutableListOf()
	}

	fun getStories() {
		NetworkApi.Api.service.getStories().enqueue(object : Callback<MutableList<NewStoriesVideo>> {
			override fun onResponse(
				call: Call<MutableList<NewStoriesVideo>>,
				response: Response<MutableList<NewStoriesVideo>>
			) {
				Log.i("StoriesFetch", response.message())
				_newStoriesVideo.value = response.body()
			}

			override fun onFailure(call: Call<MutableList<NewStoriesVideo>>, t: Throwable) {
				Log.i("StoriesFetch", t.message.toString())
			}
		})
	}
}
