package com.example.videoApp.fragments

import android.app.DownloadManager.Request.NETWORK_WIFI
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.videoApp.R
import com.example.videoApp.databinding.FragmentServerSampleBinding
import com.example.videoApp.utils.DemoUtil
import com.example.videoApp.utils.VideoAppDownloadService
import com.example.videoApp.viewModels.ServerSampleViewModel
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory
import com.google.android.exoplayer2.offline.DownloadHelper
import com.google.android.exoplayer2.offline.DownloadRequest
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.offline.StreamKey
import com.google.android.exoplayer2.scheduler.Requirements
import com.google.android.exoplayer2.scheduler.Requirements.NETWORK
import com.google.android.exoplayer2.source.BehindLiveWindowException
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.DefaultHlsExtractorFactory
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.util.Util
import java.io.IOException
import java.util.function.UnaryOperator


class ServerSampleFragment : Fragment() {

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
		val cacheDataSourceFactory: DataSource.Factory? = DemoUtil.getDownloadCache(context!!)?.let {
			CacheDataSource.Factory()
				.setCache(it)
				.setUpstreamDataSourceFactory(
					DemoUtil.getHttpDataSourceFactory(context!!)
				)
				.setCacheWriteDataSinkFactory(null) // Disable writing.
		}

		player = SimpleExoPlayer.Builder(context!!)
			.setMediaSourceFactory(
				DefaultMediaSourceFactory(cacheDataSourceFactory!!)
			)
			.build()
		binding.playerView.player = player

		when(viewModel.dataPath.value) {
			"HLS" -> {
				val downloadHelper = DownloadHelper.forMediaItem(
					context!!,
					MediaItem.fromUri(viewModel.hlsPath.value!!.toUri()),
					DefaultRenderersFactory(context!!),
					DemoUtil.getDataSourceFactory(context!!)
				)
				downloadHelper.prepare(DownloadHelperCallback())
			}
			"MP3" -> {
				val downloadRequest = DownloadRequest.Builder(
					viewModel.mp3Path.value!!,
					viewModel.mp3Path.value!!.toUri()
				).build()

				player.setMediaItem(downloadRequest.toMediaItem())

				DownloadService.sendAddDownload(
					context!!,
					VideoAppDownloadService::class.java,
					downloadRequest,
					false
				)
			}
			"MP4" -> {
				val downloadRequest = DownloadRequest.Builder(
					viewModel.mp4Path.value!!,
					viewModel.mp4Path.value!!.toUri()
				).build()

				player.setMediaItem(downloadRequest.toMediaItem())

				DownloadService.sendAddDownload(
					context!!,
					VideoAppDownloadService::class.java,
					downloadRequest,
					false
				)

			}

		}
	}

	/*private fun getHlsMediaSource(): HlsMediaSource {
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
	}*/

	private fun setPlayer() {
		initializePlayer()
		// Prepare the player.

		// Set the media source to be played.
		/*when(viewModel.dataPath.value) {
			"HLS" -> player.setMediaSource(getHlsMediaSource())
			"MP4" -> player.setMediaSource(getMp4MediaSource())
			"MP3" -> player.setMediaSource(getMp3MediaSource())

		}*/

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

	private inner class DownloadHelperCallback() : DownloadHelper.Callback {
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

			player.setMediaItem(downloadRequest.toMediaItem())

			DownloadService.sendAddDownload(
				context!!,
				VideoAppDownloadService::class.java,
				downloadRequest,
				false
			)
			helper.release()
		}

		private fun shouldDownload(track: Format): Boolean {
			return track.height != 240 && track.sampleMimeType.equals("video/avc", true);
		}

		override fun onPrepareError(helper: DownloadHelper, e: IOException) {
			Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show()
			Log.e("ServerSampleFragment", e.toString())
		}

	}

}
