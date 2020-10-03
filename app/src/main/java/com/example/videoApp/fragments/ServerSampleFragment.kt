package com.example.videoApp.fragments

import android.annotation.SuppressLint
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
import com.example.videoApp.viewModels.ServerSampleViewModel
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
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

		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		viewModel.dataPath.observe(this, Observer {
			when (it) {
				"HLS" -> {
					setPlayer()
					binding.playerViewContainer.visibility = View.VISIBLE
				}
				"" -> {
					releasePlayer()
					binding.playerViewContainer.visibility = View.GONE
				}
			}
		})
	}

	private fun initializePlayer() {
		player = SimpleExoPlayer.Builder(context!!).build()
		binding.playerView.player = player

		player.setPlayWhenReady(playWhenReady);
		player.seekTo(currentWindow, playbackPosition.toLong());
		//player.prepare();
	}

	private fun setPlayer() {
		initializePlayer()
		player.setMediaItem(MediaItem.fromUri(viewModel.hlsPath.value!!.toUri()));
		player.prepare()
	}

	@SuppressLint("InlinedApi")
	private fun hideSystemUi() {
		binding.playerView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
				or View.SYSTEM_UI_FLAG_FULLSCREEN
				or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
				or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
	}

	private fun releasePlayer() {
		if (player != null) {
			playWhenReady = player.getPlayWhenReady();
			playbackPosition = player.getCurrentPosition().toInt();
			currentWindow = player.getCurrentWindowIndex();
			player.release();
		}
	}

	override fun onStart() {
		super.onStart()
		if (Util.SDK_INT >= 24) {
			initializePlayer()
		}
	}

	override fun onResume() {
		super.onResume()
		hideSystemUi()
		if (Util.SDK_INT < 24 || player == null) {
			initializePlayer()
		}
	}

	override fun onPause() {
		super.onPause()
		if (Util.SDK_INT < 24) {
			releasePlayer()
		}
	}

	override fun onStop() {
		super.onStop()
		if (Util.SDK_INT >= 24) {
			releasePlayer()
		}
	}

}
