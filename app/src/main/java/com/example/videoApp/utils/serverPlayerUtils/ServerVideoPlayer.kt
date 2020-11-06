package com.example.videoApp.utils.serverPlayerUtils

import android.content.Context
import android.util.Log
import com.example.videoApp.databinding.FragmentServerSampleBinding
import com.example.videoApp.utils.DemoUtil
import com.example.videoApp.viewModels.ServerSampleViewModel
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.BehindLiveWindowException
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource

abstract class ServerVideoPlayer(
	private val binding: FragmentServerSampleBinding,
	private val viewModel: ServerSampleViewModel,
	private val context: Context?
) {
	lateinit var player: SimpleExoPlayer
	lateinit var cacheDataSourceFactory: DataSource.Factory

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
		binding.playerView.player = player
	}

	fun onPlayerResume() {
		//TODO
	}

	abstract fun setPlayerMediaSource()

	private fun initializePlayer() {
		createCacheDataSourceFactory()
		initPlayer()

		setPlayerMediaSource()

	}

	private fun setupTitles() {
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
		viewModel.postHlsText(str)

	}

	fun setPlayer() {
		initializePlayer()
		player.prepare()
		player.playWhenReady = true

		player.addListener(PlayerEventListener())

	}

	private inner class PlayerEventListener: Player.EventListener {
		override fun onPlaybackStateChanged(state: Int) {
			when(state) {
				Player.STATE_READY -> {
					if (viewModel.dataPath.value === "HLS") setupTitles()
				}
			}
			super.onPlaybackStateChanged(state)
		}

		override fun onPlayerError(error: ExoPlaybackException) {
			if (isBehindLiveWindow(error)) {
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
