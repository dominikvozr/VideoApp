package com.example.videoApp.utils


import android.content.Context
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.example.videoApp.views.VideoAppPlayerView
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.offline.DownloadRequest
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSource

class ProgressiveAppPlayerViewAdapter {
	companion object {
		private lateinit var cacheDataSourceFactory : CacheDataSource.Factory
		private lateinit var progressiveContext: Context
		private var progressivePlayer : SimpleExoPlayer? = null

		fun releasePlayer(){
			progressivePlayer?.release()
		}

		fun resumePlayer() {
			progressivePlayer?.playWhenReady = true
		}

		fun pausePlayer() {
			progressivePlayer?.playWhenReady = false
		}

		@JvmStatic
		@BindingAdapter(value = ["progressive_source_url"], requireAll = true)
		fun VideoAppPlayerView.loadProgressiveVideo(url: String) {
			progressiveContext = context
			this.sourcePath = url

			createCacheDataSourceFactory()
			initPlayer()

			val downloadRequest = DownloadRequest.Builder(
				url,
				url.toUri()
			).build()
			progressivePlayer?.setMediaItem(downloadRequest.toMediaItem())
			DownloadService.sendAddDownload(
				context,
				VideoAppDownloadService::class.java,
				downloadRequest,
				false
			)

			progressivePlayer?.prepare()
			this.player = progressivePlayer!!
			this.setPlayer()
			this.setPlayerEventListener()
			hls_text = this.hls_text
		}

		private fun createCacheDataSourceFactory(){
			cacheDataSourceFactory = DemoUtil.getDownloadCache(
				progressiveContext
			)?.let {
				CacheDataSource.Factory()
					.setCache(it)
					.setUpstreamDataSourceFactory(
						DemoUtil.getHttpDataSourceFactory()
					)
			}!!
		}

		fun initPlayer() {
			progressivePlayer = SimpleExoPlayer.Builder(progressiveContext)
				.setMediaSourceFactory(
					DefaultMediaSourceFactory(cacheDataSourceFactory)
				)
				.build()
		}
	}
}
