package com.example.videoApp.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.videoApp.R
import com.example.videoApp.utils.DemoUtil
import com.example.videoApp.utils.HlsAppPlayerViewAdapter
/*import com.example.videoApp.utils.serverPlayerUtils.HlsVideoPlayer
import com.example.videoApp.utils.serverPlayerUtils.ProgressiveVideoPlayer*/
import com.example.videoApp.utils.serverPlayerUtils.ServerVideoPlayer
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.BehindLiveWindowException
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.cache.CacheDataSource

class VideoAppPlayerView : ConstraintLayout {

	lateinit var player: SimpleExoPlayer

	lateinit var hls_text: TextView
	lateinit var playerView: PlayerView
	lateinit var progressBar: ProgressBar

	var sourcePath = ""

	@JvmOverloads
	constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
			: super(context, attrs, defStyleAttr) {
		init()
	}

	fun init() {
		val layout =
			LayoutInflater.from(context).inflate(R.layout.video_app_player_view, this, false);
		addView(layout)
		hls_text = layout.findViewById(R.id.hls_text)
		playerView = layout.findViewById(R.id.player_view)
		progressBar = layout.findViewById(R.id.progress_bar)
	}

	fun setPlayer() {
		playerView.player = player
	}

	fun setPlayerEventListener() {
		player.addListener(PlayerEventListener())
	}


	private inner class PlayerEventListener: Player.EventListener {
		override fun onPlaybackStateChanged(state: Int) {
			when(state) {
				Player.STATE_READY -> {
					progressBar.visibility = View.GONE
					if (sourcePath.takeLast(5) == ".m3u8") setupTitles()
				}
				Player.STATE_BUFFERING -> {
					progressBar.visibility = View.VISIBLE
				}
			}
			super.onPlaybackStateChanged(state)
		}

		override fun onPlayerError(error: ExoPlaybackException) {
			if (isBehindLiveWindow(error)) {
				setPlayer()
			} else {
				Log.e("VideoAppPlayerView", error.toString())
			}
			super.onPlayerError(error)
		}
	}

	private fun setupTitles() {
		hls_text.visibility = View.VISIBLE
		val duration: Long = player.duration
		Log.i("VideoAppPlayerView", "video duration $duration")
		hls_text.text = "video text sec. 0"
		for (x in 0..duration step 5000) {
			player.createMessage { _: Int, _: Any? -> videoChanged(x) }
				.setPosition(x).setDeleteAfterDelivery(false).send()
		}
	}

	private fun videoChanged(x: Long) {
		val str = "video text sec. $x"
		Log.i("VideoAppPlayerView", "video text sec. $x")
		hls_text.text = str

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
}
