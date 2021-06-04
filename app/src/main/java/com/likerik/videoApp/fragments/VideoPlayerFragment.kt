package com.likerik.videoApp.fragments

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.likerik.videoApp.R
import com.likerik.videoApp.databinding.FragmentVideoPlayerBinding
import com.likerik.videoApp.viewModels.VideoPlayerViewModel
import com.likerik.videoApp.viewModels.factory.VideoPlayerViewModelFactory


class VideoPlayerFragment : Fragment() {

	val args: VideoPlayerFragmentArgs by navArgs()

	lateinit var player: SimpleExoPlayer

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {

		val binding: FragmentVideoPlayerBinding = DataBindingUtil.inflate(
			inflater,
			R.layout.fragment_video_player,
			container,
			false
		)

		val videoPlayerViewModelFactory = VideoPlayerViewModelFactory(requireActivity().application ,args.videoPath)

		val videoPlayerViewModel = ViewModelProviders
			.of(this, videoPlayerViewModelFactory)
			.get(VideoPlayerViewModel::class.java)

		binding.lifecycleOwner = this
		player = SimpleExoPlayer.Builder(requireContext()).build()
		binding.playerView.player = player

		binding.playerView.useController = false

		player.setMediaItem(MediaItem.fromUri(videoPlayerViewModel.videoPath))
		player.prepare()

		player.playWhenReady = true

		binding.floatingActionSaveButton.setOnClickListener{
			videoPlayerViewModel.saveVideo()
		}

		binding.floatingActionDeleteButton.setOnClickListener{
			videoPlayerViewModel.deleteVideo()
		}

		videoPlayerViewModel.onSaveVideo.observe(viewLifecycleOwner, Observer {
			if (it == true) {
				this.findNavController()
					.navigate(VideoPlayerFragmentDirections.actionVideoPlayerFragmentToMainFragment())
				videoPlayerViewModel.navigationCompleted()
			}
		})

		videoPlayerViewModel.onDeleteVideo.observe(viewLifecycleOwner, Observer {
			if (it == true) {
				this.findNavController()
					.navigate(VideoPlayerFragmentDirections.actionVideoPlayerFragmentToCameraFragment())
				videoPlayerViewModel.navigationCompleted()
			}
		})

		setHasOptionsMenu(true)
        return binding.root
    }

	override fun onDestroyView() {
		super.onDestroyView()
		player.stop()
		player.release()
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		super.onCreateOptionsMenu(menu, inflater)
		inflater.inflate(R.menu.menu, menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return NavigationUI.onNavDestinationSelected(
			item,
			requireView().findNavController()
		) || super.onOptionsItemSelected(item)
	}
}
