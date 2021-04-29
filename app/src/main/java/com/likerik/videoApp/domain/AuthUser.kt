package com.likerik.videoApp.domain

import android.util.Log
import com.likerik.videoApp.network.NetworkApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class AuthUser (
	var userID: String?,
	val appID: String,
	val appSecret: String,
	var refreshToken: String?,
	var accessToken: String?,
	var email: String,
	var password: String,
	var deviceID: String,
	var username: String?
		) {
	companion object {
		var dominik: AuthUser = ConcreteUserBuilder()
			.withEmail("dominik@mail.sk")
			.withPassword("dominik")
			.withAppID("a1f3acec-db41-44c0-adc2-d04ba301dc60-4838cf0e-4610-4142-b8ab-b008e4d8dcfc")
			.withAppSecret("POKMKNBcdejfIDFJi4rj39r239j((Fj9(#RJ(!@(R(fjdksdf<DC45465")
			.withDeviceID("a1f3acec-db41-44c0-adc2-d04ba301dc60")
			.build()

		fun login(callback : () -> Unit? = {}) {
			NetworkApi.Api.service.login(dominik).enqueue( object :
				Callback<AuthUser> {
				override fun onResponse(call: Call<AuthUser>, response: Response<AuthUser>) {
					Log.i("HLSLogin", "Logged in")
					dominik.refreshToken = response.body()?.refreshToken
					dominik.accessToken = response.body()?.accessToken
					if (dominik.username == null)
						dominik.username = response.body()?.username
					if (dominik.userID == null)
						dominik.userID = response.body()?.userID
					callback()
				}

				override fun onFailure(call: Call<AuthUser>, t: Throwable) {
					Log.i("HLSLogin", t.message.toString())
				}
			})
		}


	}
}

interface UserBuilder {
	fun withId(id: String) : UserBuilder
	fun withAppID(appID: String) : UserBuilder
	fun withAppSecret(appSecret: String) : UserBuilder
	fun withRefreshToken(refreshToken: String) : UserBuilder
	fun withAccessToken(accessToken: String) : UserBuilder
	fun withEmail(email: String) : UserBuilder
	fun withPassword(password: String) : UserBuilder
	fun withDeviceID(deviceID: String) : UserBuilder
	fun withUsername(username: String) : UserBuilder
	fun build() : AuthUser
}

class ConcreteUserBuilder: UserBuilder {
	private var userID: 		String? = null
	private var appID: 			String? = null
	private var appSecret: 		String? = null
	private var refreshToken: 	String? = null
	private var accessToken: 	String? = null
	private var email: 			String? = null
	private var password: 		String? = null
	private var deviceID: 		String? = null
	private var username: 		String? = null

	override fun withId(id: String): UserBuilder {
		this.userID = id
		return this
	}

	override fun withAppID(appID: String): UserBuilder {
		this.appID = appID
		return this
	}

	override fun withAppSecret(appSecret: String): UserBuilder {
		this.appSecret = appSecret
		return this
	}

	override fun withRefreshToken(refreshToken: String): UserBuilder {
		this.refreshToken = refreshToken
		return this
	}

	override fun withAccessToken(accessToken: String): UserBuilder {
		this.accessToken = accessToken
		return this
	}

	override fun withEmail(email: String): UserBuilder {
		this.email = email
		return this
	}

	override fun withPassword(password: String): UserBuilder {
		this.password = password
		return this
	}

	override fun withDeviceID(deviceID: String): UserBuilder {
		this.deviceID = deviceID
		return this
	}

	override fun withUsername(username: String): UserBuilder {
		this.username = username
		return this
	}

	override fun build(): AuthUser {
		return AuthUser(userID, appID!!, appSecret!!, refreshToken, accessToken, email!!, password!!, deviceID!!, username)
	}
}
