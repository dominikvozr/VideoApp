package com.example.videoApp.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.videoApp.R
import com.example.videoApp.utils.serverPlayerUtils.HlsVideoPlayer
import com.example.videoApp.utils.serverPlayerUtils.ProgressiveVideoPlayer
import com.example.videoApp.utils.serverPlayerUtils.ServerVideoPlayer
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.SimpleExoPlayer

class VideoAppPlayerView : ConstraintLayout {

	private var serverVideoPlayer: ServerVideoPlayer? = null

	private lateinit var hls_text: TextView
	private lateinit var close_btn: ImageButton

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
		close_btn = layout.findViewById(R.id.close_btn)

	}

	/*private fun sourceDecision(type: String) {
		when(type) {
			"HLS" -> {
				hls_text.visibility = View.VISIBLE
				serverVideoPlayer = HlsVideoPlayer(binding, viewModel, context)
				serverVideoPlayer!!.setPlayer()
				//binding.playerViewContainer.visibility = View.VISIBLE
				//binding.buttonsContainer.visibility = View.GONE
			}
			"MP3" -> {
				hls_text.visibility = View.GONE
				serverVideoPlayer = ProgressiveVideoPlayer(binding, viewModel, context, viewModel.mp3Path.value)
				serverVideoPlayer!!.setPlayer()
				//binding.playerViewContainer.visibility = View.VISIBLE
				//binding.buttonsContainer.visibility = View.GONE
			}
			"MP4" -> {
				hls_text.visibility = View.GONE
				serverVideoPlayer = ProgressiveVideoPlayer(binding, viewModel, context, viewModel.mp4Path.value)
				serverVideoPlayer!!.setPlayer()
				//binding.playerViewContainer.visibility = View.VISIBLE
				//binding.buttonsContainer.visibility = View.GONE
			}
			"" -> {
				serverVideoPlayer!!.releasePlayer()
				//binding.playerViewContainer.visibility = View.GONE
				//binding.buttonsContainer.visibility = View.VISIBLE
			}
		}
	}*/

	fun setPlayer() {
		serverVideoPlayer?.let {
			it.setPlayer()
		}
	}

	fun play() {
		serverVideoPlayer?.let {
			it.setPlayWhenReady(true)
		}
	}

	fun pause() {
		serverVideoPlayer?.let {
			it.setPlayWhenReady(true)
		}
	}

	fun release() {
		serverVideoPlayer?.let {
			it.releasePlayer()
		}
	}

}
