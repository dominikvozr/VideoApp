package com.example.videoApp.utils.serverPlayerUtils

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.videoApp.R
import com.example.videoApp.databinding.FragmentServerSampleBinding
import com.example.videoApp.utils.DemoUtil
import com.example.videoApp.viewModels.ServerSampleViewModel
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.BehindLiveWindowException
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import kotlinx.android.synthetic.main.list_item_video.view.*

abstract class ServerVideoPlayer/*(
	private val binding: FragmentServerSampleBinding,
	private val viewModel: ServerSampleViewModel,
	private val context: Context?
)*/ : ConstraintLayout {

	//private var binding: FragmentServerSampleBinding
	private var sourcePath: String
	lateinit var player: SimpleExoPlayer
	lateinit var cacheDataSourceFactory: DataSource.Factory

	private lateinit var hls_text: TextView
	private lateinit var close_btn: ImageButton
	private lateinit var player_view: PlayerView
	private lateinit var custom_player_container: ConstraintLayout

	constructor(context: Context) : super(context) {
		this.sourcePath = ""
		init()
	}

	constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
		this.sourcePath = ""
		init()
	}

	@JvmOverloads
	constructor(
		//binding: FragmentServerSampleBinding,
		sourcePath: String,
		context: Context,
		attrs: AttributeSet? = null,
		defStyleAttr: Int = 0)
			: super(context, attrs, defStyleAttr) {
		this.sourcePath = sourcePath
		//this.viewModel = viewModel

		init()

	}

	fun init() {
		val layout =
			LayoutInflater.from(context).inflate(R.layout.video_app_player_view, this, false);
		addView(layout)

		setFocusable(true)
		setFocusableInTouchMode(true)

		hls_text = layout.findViewById(R.id.hls_text)
		close_btn = layout.findViewById(R.id.close_btn)
		player_view = layout.findViewById(R.id.player_view)
		custom_player_container = layout.findViewById(R.id.custom_player_container)
		custom_player_container.visibility = View.VISIBLE
	}


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
		player_view.player = player

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
		hls_text.text = str

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
					if (sourcePath.takeLast(4) === "m3u8") setupTitles()
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
		custom_player_container.visibility = View.GONE
		player.release()
	}

	fun setPlayWhenReady(value: Boolean) {
		player.playWhenReady = value
	}
}
