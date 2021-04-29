package com.likerik.videoApp.network

import com.likerik.videoApp.domain.AuthUser
import com.likerik.videoApp.domain.NewStoriesVideo
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


class NetworkApi {
	object Api {

		private const val BASE_URL = "https://likerik.com/"

		private val retrofit = Retrofit.Builder()
				.addConverterFactory(GsonConverterFactory.create())
				.baseUrl(BASE_URL)
				.build()

		interface ApiService {
			@Headers("Content-type: application/json", "Accept: application/json")
			@POST("/token/refresh")
			fun refreshToken(@Body user: AuthUser):
					Call<AuthUser>

			@Headers("Content-type: application/json", "Accept: application/json", "Cache-Control: no-cache")
			@POST("/user/login")
			fun login(@Body user: AuthUser):
					Call<AuthUser>

			@Headers("Content-type: application/json", "Accept: application/json")
			@GET("videos.json")
			fun getStories():
					Call<MutableList<NewStoriesVideo>>
		}

		val service : ApiService by lazy {
			retrofit.create(ApiService::class.java)
		}
	}
}
