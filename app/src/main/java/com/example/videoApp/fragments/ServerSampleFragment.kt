package com.example.videoApp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.videoApp.R
import com.example.videoApp.databinding.FragmentServerSampleBinding
import com.example.videoApp.utils.VideoAppDownloadService
import com.example.videoApp.viewModels.ServerSampleViewModel
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory
import com.google.android.exoplayer2.offline.DownloadRequest
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.source.BehindLiveWindowException
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.DefaultHlsExtractorFactory
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util


class ServerSampleFragment : Fragment() {

	private var playWhenReady = true
	private var currentWindow = 0
	private var playbackPosition = 0

	private lateinit var binding: FragmentServerSampleBinding

	private val viewModel: ServerSampleViewModel by lazy {
		ViewModelProviders.of(this).get(ServerSampleViewModel::class.java)
	}

	lateinit var player: SimpleExoPlayer

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		binding = DataBindingUtil.inflate(
			inflater,
			R.layout.fragment_server_sample,
			container,
			false
		)
		binding.viewModel = viewModel
		binding.lifecycleOwner = this

		// start service .... check downloaded, if not... than download mp3, mp4, m3u8
		/*val downloadRequest: DownloadRequest = DownloadRequest.Builder(
			viewModel.hlsPath.value!!,
			viewModel.hlsPath.value!!.toUri()
		).build()

		DownloadService.sendAddDownload(
			context!!,
			VideoAppDownloadService::class.java,
			downloadRequest,
			false
		)*/

		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		viewModel.dataPath.observe(this, Observer {

			if (it === "HLS" || it === "MP3" || it === "MP4") {
				setPlayer()
				binding.playerViewContainer.visibility = View.VISIBLE
				binding.buttonsContainer.visibility = View.GONE
			} else if (it === "") {
				releasePlayer()
				binding.playerViewContainer.visibility = View.GONE
				binding.buttonsContainer.visibility = View.VISIBLE
			}
		})
	}



	private fun initializePlayer() {
		player = SimpleExoPlayer.Builder(context!!).build()
		binding.playerView.player = player
	}

	private fun getHlsMediaSource(): HlsMediaSource {
		// Create a data source factory.
		val dataSourceFactory: DataSource.Factory = DefaultHttpDataSourceFactory()

		val defaultHlsExtractorFactory =
			DefaultHlsExtractorFactory(
				DefaultTsPayloadReaderFactory.FLAG_ALLOW_NON_IDR_KEYFRAMES,
				false
			)
		// Create a HLS media source pointing to a playlist uri.
		return HlsMediaSource.Factory(dataSourceFactory)
			.setExtractorFactory(defaultHlsExtractorFactory)
			.setAllowChunklessPreparation(true)
			.createMediaSource(MediaItem.fromUri(viewModel.hlsPath.value!!.toUri()))
	}

	private fun getMp4MediaSource(): ProgressiveMediaSource {
		val dataSourceFactory: DataSource.Factory = DefaultHttpDataSourceFactory()
		// This is the MediaSource representing the media to be played.
		return ProgressiveMediaSource.Factory(dataSourceFactory)
			.createMediaSource(MediaItem.fromUri(viewModel.mp4Path.value!!.toUri()))
	}

	private fun getMp3MediaSource(): ProgressiveMediaSource {
		val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
			context!!,
			Util.getUserAgent(context!!, "VideoApp")
		)
		// This is the MediaSource representing the media to be played.
		return ProgressiveMediaSource.Factory(dataSourceFactory)
			.createMediaSource(MediaItem.fromUri(viewModel.mp3Path.value!!.toUri()))
	}

	private fun setPlayer() {
		initializePlayer()
		// Set the media source to be played.
		when(viewModel.dataPath.value) {
			"HLS" -> player.setMediaSource(getHlsMediaSource())
			"MP4" -> player.setMediaSource(getMp4MediaSource())
			"MP3" -> player.setMediaSource(getMp3MediaSource())

		}
		// Prepare the player.
		player.prepare()

		player.playWhenReady = true
	}

	private fun releasePlayer() {
		player.release()
	}

	override fun onStart() {
		super.onStart()
			initializePlayer()
	}

	override fun onResume() {
		super.onResume()
		player.playWhenReady = true
	}

	override fun onPause() {
		super.onPause()
		player.playWhenReady = false
	}

	override fun onStop() {
		super.onStop()
		player.playWhenReady = false
	}

	override fun onDestroy() {
		super.onDestroy()
		releasePlayer()
	}

	fun onPlayerError(e: ExoPlaybackException) {
		if (isBehindLiveWindow(e)) {
			// Re-initialize player at the live edge.
		} else {
			// Handle other errors
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

}
