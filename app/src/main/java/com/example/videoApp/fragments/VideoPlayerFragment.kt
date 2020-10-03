package com.example.videoApp.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.videoApp.R
import com.example.videoApp.databinding.FragmentVideoPlayerBinding
import com.example.videoApp.viewModels.VideoPlayerViewModel
import com.example.videoApp.viewModels.factory.VideoPlayerViewModelFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util


class VideoPlayerFragment : Fragment() {

	val args: VideoPlayerFragmentArgs by navArgs()

	lateinit var player: SimpleExoPlayer

	override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

		val binding: FragmentVideoPlayerBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_video_player, container, false)

		val videoPlayerViewModelFactory = VideoPlayerViewModelFactory(activity!!.application ,args.videoPath)

		val videoPlayerViewModel = ViewModelProviders
			.of(this, videoPlayerViewModelFactory)
			.get(VideoPlayerViewModel::class.java)

		binding.lifecycleOwner = this
		player = SimpleExoPlayer.Builder(context!!).build()
		binding.playerView.player = player

		binding.playerView.useController = false
		// Produces DataSource instances through which media data is loaded.
		val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
			context!!,
			Util.getUserAgent(context!!, "VideoApp")
		)

		// This is the MediaSource representing the media to be played.
		val videoSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
			.createMediaSource(Uri.parse(videoPlayerViewModel.videoPath))

		// Prepare the player with the source.
		player.prepare(videoSource)

		player.playWhenReady = true

		binding.floatingActionSaveButton.setOnClickListener{
			videoPlayerViewModel.saveVideo()
		}

		binding.floatingActionDeleteButton.setOnClickListener{
			videoPlayerViewModel.deleteVideo()
		}

		videoPlayerViewModel.onSaveVideo.observe(this, Observer {
			if (it == true) {
				this.findNavController().navigate(VideoPlayerFragmentDirections.actionVideoPlayerFragmentToMainFragment())
				videoPlayerViewModel.navigationCompleted()
			}
		})

		videoPlayerViewModel.onDeleteVideo.observe(this, Observer {
			if (it == true) {
				this.findNavController().navigate(VideoPlayerFragmentDirections.actionVideoPlayerFragmentToCameraFragment())
				videoPlayerViewModel.navigationCompleted()
			}
		})
        return binding.root
    }



	override fun onDestroyView() {
		super.onDestroyView()
		player.stop()
		player.release()
	}
}
