package com.example.videoApp.utils.serverPlayerUtils

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.videoApp.utils.DemoUtil
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.BehindLiveWindowException
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource

abstract class ServerVideoPlayer : ConstraintLayout {
	lateinit var player: SimpleExoPlayer
	lateinit var cacheDataSourceFactory: CacheDataSource.Factory
	var progressBarShown = true

	constructor(context: Context) : super(context)

	constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

	private fun createCacheDataSourceFactory(){
		cacheDataSourceFactory = DemoUtil.getDownloadCache(context!!)?.let {
			CacheDataSource.Factory()
				.setCache(it)
				.setUpstreamDataSourceFactory(
					DemoUtil.getHttpDataSourceFactory()
				)
		}!!
	}

	fun initPlayer() {
		player = SimpleExoPlayer.Builder(context!!)
			.setMediaSourceFactory(
				DefaultMediaSourceFactory(cacheDataSourceFactory!!)
			)
			.build()
	}

	abstract fun setPlayerMediaSource()

	private fun initializePlayer() {
		createCacheDataSourceFactory()
		initPlayer()
		setPlayerMediaSource()
	}

	fun setPlayer() {
		initializePlayer()
		player.prepare()
		player.playWhenReady = true
		player.addListener(PlayerEventListener())

	}

	/*private fun setupTitles() {
		val duration: Long = player.duration
		Log.i("ServerSampleFragment", "video duration $duration")
		for (x in 0..duration step 5000) {
			player?.createMessage { _: Int, _: Any? -> videoChanged(x) }
				?.setPosition(x)?.setDeleteAfterDelivery(false)?.send()
		}
	}

	private fun videoChanged(x: Long) {
		val str = "video text sec. $x"
		Log.i("ServerSampleFragment", "video text sec. $x")
		hls_text.text = str

	}*/

	private inner class PlayerEventListener: Player.EventListener {
		/*override fun onPlaybackStateChanged(state: Int) {
			when(state) {
				Player.STATE_READY -> {
					if (sourcePath.takeLast(4) === "m3u8") setupTitles()
				}
			}
			super.onPlaybackStateChanged(state)
		}*/

		override fun onPlayerError(error: ExoPlaybackException) {
			if (isBehindLiveWindow(error)) {
				progressBarShown = true
				setPlayer()
			} else {
				Log.e("ServerVideoPlayer", error.toString())
			}
			super.onPlayerError(error)
		}
	}

	private fun isBehindLiveWindow(e: ExoPlaybackException): Boolean {
		if (e.type !== ExoPlaybackException.TYPE_SOURCE) {
			return false
		}
		var cause: Throwable? = e.getSourceException()
		while (cause != null) {
			if (cause is BehindLiveWindowException) {
				return true
			}
			cause = cause.cause
		}
		return false
	}

	fun releasePlayer() {
		player.release()
	}

	fun setPlayWhenReady(value: Boolean) {
		player.playWhenReady = value
	}
}
