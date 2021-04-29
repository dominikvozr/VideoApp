package com.likerik.videoApp.utils.stories

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import androidx.databinding.BindingAdapter
import com.likerik.videoApp.domain.NewStoriesVideo
import com.likerik.videoApp.utils.PlayerStateCallback
import com.likerik.videoApp.views.StoriesPlayerView
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector.ParametersBuilder
import com.google.android.exoplayer2.upstream.DefaultAllocator
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.uxcam.UXCam.logEvent
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


class StoriesPlayerViewAdapter {
	companion object {
		var playlist : NewStoriesVideo? = null
		private var hlsPlayer : SimpleExoPlayer? = null
		private lateinit var cacheDataSourceFactory : CacheDataSource.Factory

		// for hold all players generated
		var playersMap : MutableMap<Int, SimpleExoPlayer> = mutableMapOf()
		// for check released players
		var playersReleased : MutableMap<Int, Boolean> = mutableMapOf()
		// for hold current player
		var currentPlayingVideo : Pair<Int, SimpleExoPlayer>? = null

		var currentvolume: Float = currentPlayingVideo?.second?.volume ?: 0f

		fun releaseAllPlayers(){
			playersMap.map {
				it.value.release()
			}
		}

		fun releasePlayer(index: Int){
			playersMap[index]?.release()
			playersReleased[index] = true
			//Log.i("STORIES", playersReleased.toString())
		}

		fun toggleSound(){
			currentPlayingVideo?.let {
				if (it.second.volume != 0f) {
					currentvolume = it.second.volume
					it.second.volume = 0f
				} else {
					it.second.volume = currentvolume
					currentvolume = 0f
				}
			}
		}

		fun pauseCurrentPlayingVideo(){
			if (currentPlayingVideo != null){
				currentPlayingVideo?.second?.playWhenReady = false
			}
		}

		fun playVideoOnIndex(index: Int) {
			val video = playersMap.get(index)
			pauseCurrentPlayingVideo()
			if (video?.playWhenReady == false) {
				if (video.currentPosition >= video.duration)
					video.seekTo(0)
				video.playWhenReady = true
				currentPlayingVideo = Pair(index, video)
			}
		}

		@JvmStatic
		@BindingAdapter(value = ["video_url", "on_state_change", "item_index"], requireAll = true)
		fun StoriesPlayerView.loadVideo(
			playlist: NewStoriesVideo,
			callback: PlayerStateCallback,
			item_index: Int? = null
		) {
			Companion.playlist = playlist
			createCacheDataSourceFactory(context)
			initPlayer(context)

			this.playlist = playlist

			/*val mediaSource: ProgressiveMediaSource =
				ProgressiveMediaSource.Factory(cacheDataSourceFactory)
					.createMediaSource(MediaItem.fromUri(playlist.link))
			hlsPlayer!!.setMediaSource(mediaSource)*/
			hlsPlayer!!.setMediaItem(MediaItem.fromUri(playlist.link_480p_tinyfied))
			hlsPlayer!!.prepare()

			hlsPlayer!!.playWhenReady = false
			currentPlayingVideo?.second?.let {
				hlsPlayer!!.volume = it.volume
			}
			binding.playerView.player = hlsPlayer
			binding.playerView.setKeepContentOnPlayerReset(true)
			binding.playerView.useController = false

			if (playersMap.containsKey(item_index)) {
				//playersMap[item_index]?.release()
				playersMap.remove(item_index)
			}

			if (item_index != null) {
				playersMap[item_index] = hlsPlayer!!
				playersReleased[item_index] = false
			}

			this.setupTitles(hlsPlayer!!)

			hlsPlayer!!.addListener(object : Player.EventListener {

				override fun onPlayerError(error: ExoPlaybackException) {
					Log.e("STORIES", error.toString())
					logEvent("Error")
					/*if (state == "fetching data") return
					val state = hlsPlayer!!.playbackState
					loadVideo(playlist, callback, item_index)
					hlsPlayer!!.seekTo(state.toLong())
					hlsPlayer!!.playWhenReady = true*/
					//Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
				}

				override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {

					if (playbackState == Player.STATE_BUFFERING) {
						callback.onVideoBuffering(hlsPlayer!!)
						binding.progressBar.visibility = View.VISIBLE
						logEvent("Buffering")
					}
					if (playbackState == Player.STATE_READY) {
						binding.progressBar.visibility = View.GONE
						callback.onVideoDurationRetrieved(hlsPlayer!!.duration, hlsPlayer!!)
						logEvent("Playing")
					}

					if (playbackState == Player.STATE_READY && hlsPlayer!!.playWhenReady) {
						callback.onStartedPlaying(hlsPlayer!!)
						logEvent("Playing")
					}

					if (playbackState == Player.STATE_ENDED) {
						callback.onFinishedPlaying(hlsPlayer!!, item_index!!)
					}

				}
			})
		}

		private fun createCacheDataSourceFactory(context: Context){
			cacheDataSourceFactory = DemoUtil.getDownloadCache(context)?.let {
				CacheDataSource.Factory()
					.setCache(it)
					.setUpstreamDataSourceFactory(
						DemoUtil.getHttpDataSourceFactory()
					)//.setCacheWriteDataSinkFactory(null) // Disable writing.
			}!!
		}

		@Throws(IOException::class)
		fun drawableFromUrl(url: String?): Drawable? {
			val x: Bitmap
			val connection = URL(url).openConnection() as HttpURLConnection
			connection.connect()
			val input = connection.inputStream
			x = decodeStream(input)
			return BitmapDrawable(Resources.getSystem(), x)
		}

		@Throws(MalformedURLException::class, IOException::class)
		private fun drawable_from_url(url: String?): Bitmap? {
			val connection = URL(url).openConnection() as HttpURLConnection
			connection.setRequestProperty("User-agent", "Mozilla/4.0")
			connection.connect()
			val input = connection.inputStream
			return decodeStream(input)
		}

		private fun initPlayer(context: Context) {

			val trackSelectionFactory = AdaptiveTrackSelection.Factory()
			val trackSelector = DefaultTrackSelector(trackSelectionFactory)

			val trackSelectorParameters = ParametersBuilder().build()
			trackSelector.setParameters(trackSelectorParameters)

			val bandwidthMeter = DefaultBandwidthMeter.Builder(context)
				.setInitialBitrateEstimate(10)
				.build()

			val loadControl: LoadControl = DefaultLoadControl.Builder()
				.setAllocator(DefaultAllocator(true, 16))
				.setBufferDurationsMs(
					VideoPlayerConfig.MIN_BUFFER_DURATION,
					VideoPlayerConfig.MAX_BUFFER_DURATION,
					VideoPlayerConfig.MIN_PLAYBACK_START_BUFFER,
					VideoPlayerConfig.MIN_PLAYBACK_RESUME_BUFFER
				)
				.setTargetBufferBytes(-1)
				.setPrioritizeTimeOverSizeThresholds(true).createDefaultLoadControl()

			//experimental_setMediaCodecOperationMode()
			val renderersFactory = DefaultRenderersFactory(context)
				//.experimentalSetForceAsyncQueueingSynchronizationWorkaround(true)
				//.experimentalSetAsynchronousBufferQueueingEnabled(true)
				//.experimentalSetSynchronizeCodecInteractionsWithQueueingEnabled(true)

			hlsPlayer = SimpleExoPlayer.Builder(context/*, renderersFactory*/)
				.setLoadControl(loadControl)
				.setTrackSelector(trackSelector)
				.setBandwidthMeter(bandwidthMeter)
				.setMediaSourceFactory(
					DefaultMediaSourceFactory(cacheDataSourceFactory)
				)
				.build()


			/*hlsPlayer = SimpleExoPlayer.Builder(
				context,
				DefaultRenderersFactory(context)

			).setLoadControl(
				DefaultLoadControl.Builder()
					.setPrioritizeTimeOverSizeThresholds(false)
					.createDefaultLoadControl()
			)
			*//*.setMediaSourceFactory(
				DefaultMediaSourceFactory(cacheDataSourceFactory)
			)*//*
				.build()*/
		}

	}
}
// PRED PARAMS
object VideoPlayerConfig {
	//Minimum Video you want to buffer while Playing
	const val MIN_BUFFER_DURATION = 2000

	//Max Video you want to buffer during PlayBack
	const val MAX_BUFFER_DURATION = 15000

	//Min Video you want to buffer before start Playing it
	const val MIN_PLAYBACK_START_BUFFER = 2000

	//Min video You want to buffer when user resumes video
	const val MIN_PLAYBACK_RESUME_BUFFER = 2000
}

// PO PARAMS
/*object VideoPlayerConfig {
	//Minimum Video you want to buffer while Playing
	const val MIN_BUFFER_DURATION = 1000

	//Max Video you want to buffer during PlayBack
	const val MAX_BUFFER_DURATION = 10000

	//Min Video you want to buffer before start Playing it
	const val MIN_PLAYBACK_START_BUFFER = 500

	//Min video You want to buffer when user resumes video
	const val MIN_PLAYBACK_RESUME_BUFFER = 1000
}*/
