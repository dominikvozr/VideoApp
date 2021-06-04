package com.likerik.videoApp.utils.main

import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.databinding.BindingAdapter
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.SimpleExoPlayer
import com.likerik.videoApp.utils.PlayerStateCallback
import com.likerik.videoApp.views.VideoAppPlayerView


class PlayerViewAdapter {

	companion object {
		private var playersMap    : MutableMap<Int, SimpleExoPlayer> = mutableMapOf()
		private var playersInfo   : MutableMap<Int, Pair< Boolean, Pair< Uri, PlayerStateCallback >>> = mutableMapOf()
		var currentPlayingVideo   : Pair<Int, SimpleExoPlayer>? = null
		private var currentvolume : Float = currentPlayingVideo?.second?.volume ?: 0f

		fun releaseAllPlayers(){
			playersMap.map {
				it.value.release()
			}

			playersInfo.map {
				playersInfo[it.key] = it.value.copy(first = true)
			}
		}

		fun releasePlayer(index: Int){
			playersMap[index]?.release()

			playersInfo[index]?.let {
				playersInfo[index] = it.copy(first = true)
			}
		}

		fun toggleSound(){
			currentPlayingVideo?.let {
				if (it.second.volume != 0f) {
					it.second.volume = 0f
				} else {
					it.second.volume = currentvolume
				}
			}
		}

		fun pauseCurrentPlayingVideo(){
			if (currentPlayingVideo != null){
				currentPlayingVideo?.second?.playWhenReady = false
				//currentPlayingVideo?.first?.let { releasePlayer(it) }
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
		fun VideoAppPlayerView.loadVideo(
			uri: Uri,
			callback: PlayerStateCallback,
			item_index: Int? = null
		) {

			val newPlayer = SimpleExoPlayer.Builder(context).build()

			newPlayer.setMediaItem(MediaItem.fromUri(uri))
			newPlayer.prepare()
			newPlayer.playWhenReady = false
			newPlayer.repeatMode = Player.REPEAT_MODE_ALL

			currentvolume = newPlayer.volume
			binding.playerView.player = newPlayer
			//binding.playerView.setKeepContentOnPlayerReset(true)
			binding.playerView.useController = false

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
					if (playbackState == Player.STATE_BUFFERING) {
						callback.onVideoBuffering(newPlayer)
						binding.progressBar.visibility = View.VISIBLE
						//if (item_index == 0) {
							//loadVideo(uri, callback, item_index)
							//newPlayer.seekTo(state.toLong())
							//newPlayer.playWhenReady = true
						//}
					}

					if (playbackState == Player.STATE_READY) {
						binding.progressBar.visibility = View.GONE
						callback.onVideoDurationRetrieved(newPlayer.duration, newPlayer)
					}

					if (playbackState == Player.STATE_READY && newPlayer.playWhenReady) {
						callback.onStartedPlaying(newPlayer)
					}

					/*if (playbackState == Player.STATE_ENDED) {
						loadVideo(uri, callback, item_index)
						//newPlayer.seekTo(state.toLong())
						newPlayer.playWhenReady = true
					}*/
				}
			})
		}
	}
}
