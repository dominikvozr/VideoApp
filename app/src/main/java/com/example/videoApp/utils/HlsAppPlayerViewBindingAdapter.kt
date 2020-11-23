package com.example.videoApp.utils

import android.content.Context
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.example.videoApp.utils.serverPlayerUtils.ServerVideoPlayer
import com.example.videoApp.views.VideoAppPlayerView
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.offline.DownloadHelper
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.offline.StreamKey
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import java.io.IOException

class HlsAppPlayerViewAdapter {
	companion object {
		private lateinit var hlsContext : Context
		private var hlsPlayer : SimpleExoPlayer? = null
		private lateinit var cacheDataSourceFactory : CacheDataSource.Factory

		fun releasePlayer(){
			hlsPlayer?.release()
		}

		fun resumePlayer() {
			hlsPlayer?.playWhenReady = true
		}

		fun pausePlayer() {
			hlsPlayer?.playWhenReady = false
		}

		@JvmStatic
		@BindingAdapter(value = ["hls_source_url"], requireAll = true)

		fun VideoAppPlayerView.loadHlsVideo(url: String) {
			this.sourcePath = url
			hlsContext = context
			createCacheDataSourceFactory()
			initPlayer()
			val downloadHelper = DownloadHelper.forMediaItem(
				context,
				MediaItem.fromUri(url.toUri()),
				DefaultRenderersFactory(context),
				cacheDataSourceFactory
			)
			downloadHelper.prepare(DownloadHelperCallback())
			hlsPlayer!!.prepare()
			this.player = hlsPlayer!!
			this.setPlayer()
			this.setPlayerEventListener()
			hls_text = this.hls_text
		}

		private fun createCacheDataSourceFactory(){
			cacheDataSourceFactory = DemoUtil.getDownloadCache(hlsContext)?.let {
				CacheDataSource.Factory()
					.setCache(it)
					.setUpstreamDataSourceFactory(
						DemoUtil.getHttpDataSourceFactory()
					)
			}!!
		}

		fun initPlayer() {
			hlsPlayer = SimpleExoPlayer.Builder(hlsContext)
				.setMediaSourceFactory(
					DefaultMediaSourceFactory(cacheDataSourceFactory!!)
				)
				.build()
		}

		private class DownloadHelperCallback() : DownloadHelper.Callback {
			override fun onPrepared(helper: DownloadHelper) {
				val trackList = arrayListOf<StreamKey>()
				for (i in 0 until helper.periodCount) {
					val trackGroups = helper.getTrackGroups(i)
					for (j in 0 until trackGroups.length) {
						val trackGroup = trackGroups.get(j)
						for (k in 0 until trackGroup.length) {
							val track = trackGroup.getFormat(k)
							if(shouldDownload(track)) {
								trackList.add(StreamKey(i, j, k))
							}

						}
					}
				}


				var downloadRequest = helper.getDownloadRequest(ByteArray(trackList.size))

				val mediaItem = downloadRequest.toMediaItem()

				hlsPlayer?.setMediaItem(mediaItem)
				if (trackList.isNotEmpty()) {
					DownloadService.sendAddDownload(
						hlsContext,
						VideoAppDownloadService::class.java,
						downloadRequest,
						false
					)
				}
				helper.release()
			}

			private fun shouldDownload(track: Format): Boolean {
				return track.height != 240 && track.sampleMimeType.equals("video/avc", true);
			}


			override fun onPrepareError(helper: DownloadHelper, e: IOException) {
				Toast.makeText(hlsContext, e.toString(), Toast.LENGTH_LONG).show()
				Log.e("ServerSampleFragment", e.toString())
			}
		}
	}
}
