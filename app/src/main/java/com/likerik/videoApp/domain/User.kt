package com.likerik.videoApp.domain

data class User (val firstname: String,
				 val lastname: String,
				 val username: String,
				 val actual_photo: String,
				 val _key: String,
				 val isCheered: Boolean) {
	fun getStoryDisplayUserName(): String {
		return "@ $username"
	}
}
