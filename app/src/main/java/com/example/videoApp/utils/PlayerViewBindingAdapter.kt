package com.example.videoApp.utils

import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.databinding.BindingAdapter
import com.example.videoApp.views.VideoAppPlayerView
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util


class PlayerViewAdapter {

	companion object {
		// for hold all players generated
		private var playersMap : MutableMap<Int, SimpleExoPlayer> = mutableMapOf()
		// for hold current player
		private var currentPlayingVideo : Pair<Int, SimpleExoPlayer>? = null

		private var currentvolume: Float = currentPlayingVideo?.second?.volume ?: 0f

		fun releaseAllPlayers(){
			playersMap.map {
				it.value.release()
			}
		}

		fun releasePlayer(index: Int){
			playersMap[index]?.release()
		}

		fun toggleSound(index: Int){
			currentPlayingVideo?.let {
				if (it.second.volume != 0f) {
					it.second.volume = 0f
				} else {
					it.second.volume = currentvolume
				}
			}
			//player.setVolume(0f)
			//player.setVolume(currentVolume)
		}

		private fun pauseCurrentPlayingVideo(){
			if (currentPlayingVideo != null){
				currentPlayingVideo?.second?.playWhenReady = false
			}
		}

		fun playVideoOnIndex(index: Int) {
			val video = playersMap.get(index)
			if (video?.playWhenReady == false) {
				pauseCurrentPlayingVideo()
				video.playWhenReady = true
				currentPlayingVideo = Pair(index, video)
			}
		}

		@JvmStatic
		@BindingAdapter(value = ["video_url", "on_state_change", "item_index"], requireAll = true)
		fun VideoAppPlayerView.loadVideo(uri: Uri, callback: PlayerStateCallback, item_index: Int? = null) {
			val newPlayer = SimpleExoPlayer.Builder(
				context!!,
				DefaultRenderersFactory(context)

			).setLoadControl(
					DefaultLoadControl.Builder()
						.setPrioritizeTimeOverSizeThresholds(false)
						.createDefaultLoadControl()
				)
				.build()

			newPlayer.playWhenReady = false
			newPlayer.repeatMode = Player.REPEAT_MODE_ALL
			// When changing track, retain the latest frame instead of showing a black screen
			//setKeepContentOnPlayerReset(true)
			playerView.setKeepContentOnPlayerReset(true)
			// We'll show the controller, change to true if want controllers as pause and start
			playerView.useController = false
			//this.useController = false

			// Produces DataSource instances through which media data is loaded.
			val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
				context,
				Util.getUserAgent(context!!, "VideoApp")
			)

			// This is the MediaSource representing the media to be played.
			val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
				.createMediaSource(uri)
			newPlayer.setMediaSource(mediaSource)

			newPlayer.prepare()

			this.playerView.player = newPlayer
			currentvolume = newPlayer.volume

			// add player with its index to map
			if (playersMap.containsKey(item_index))
				playersMap.remove(item_index)
			if (item_index != null)
				playersMap[item_index] = newPlayer

			newPlayer.addListener(object : Player.EventListener {

				override fun onPlayerError(error: ExoPlaybackException) {
					super.onPlayerError(error)
					Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
				}

				override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
					super.onPlayerStateChanged(playWhenReady, playbackState)

					if (playbackState == Player.STATE_BUFFERING) {
						callback.onVideoBuffering(newPlayer)
						// Buffering..
						// set progress bar visible here
						// set thumbnail visible
						/*thumbnail.visibility = View.VISIBLE*/
						progressBar.visibility = View.VISIBLE
					}

					if (playbackState == Player.STATE_READY) {
						// [PlayerView] has fetched the video duration so this is the block to hide the buffering progress bar
						progressBar.visibility = View.GONE
						// set thumbnail gone
						//thumbnail.visibility = View.GONE
						callback.onVideoDurationRetrieved(newPlayer.duration, newPlayer)
					}

					if (playbackState == Player.STATE_READY && newPlayer.playWhenReady) {
						// [PlayerView] has started playing/resumed the video
						callback.onStartedPlaying(newPlayer)
					}

				}
			})
		}
	}
}
