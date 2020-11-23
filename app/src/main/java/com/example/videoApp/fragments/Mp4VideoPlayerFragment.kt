package com.example.videoApp.fragments

import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.videoApp.R
import com.example.videoApp.databinding.FragmentMp4VideoPlayerBinding
import com.example.videoApp.utils.ProgressiveAppPlayerViewAdapter
import com.example.videoApp.viewModels.Mp4VideoPlayerViewModel

class Mp4VideoPlayerFragment : Fragment() {

	private lateinit var binding: FragmentMp4VideoPlayerBinding

	private val viewModel: Mp4VideoPlayerViewModel by lazy {
		ViewModelProviders.of(this).get(Mp4VideoPlayerViewModel::class.java)
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		binding = DataBindingUtil.inflate(
			inflater,
			R.layout.fragment_mp4_video_player,
			container,
			false
		)
		binding.viewModel = viewModel
		binding.lifecycleOwner = this
		setHasOptionsMenu(true)
		return binding.root
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val callback = object : OnBackPressedCallback(true) {
			override fun handleOnBackPressed() {
				findNavController().navigate(Mp4VideoPlayerFragmentDirections.actionMp4VideoPlayerFragmentToServerSampleFragment())
				ProgressiveAppPlayerViewAdapter.releasePlayer()
			}
		}
		requireActivity().onBackPressedDispatcher.addCallback(this, callback)
	}

	override fun onResume() {
		super.onResume()
		ProgressiveAppPlayerViewAdapter.resumePlayer()
	}

	override fun onPause() {
		super.onPause()
		ProgressiveAppPlayerViewAdapter.pausePlayer()
	}

	override fun onStop() {
		super.onStop()
		ProgressiveAppPlayerViewAdapter.pausePlayer()
	}

	override fun onDestroy() {
		super.onDestroy()
		ProgressiveAppPlayerViewAdapter.releasePlayer()
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		super.onCreateOptionsMenu(menu, inflater)
		inflater.inflate(R.menu.menu,  menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		ProgressiveAppPlayerViewAdapter.releasePlayer()
		return NavigationUI.onNavDestinationSelected(
			item,
			requireView().findNavController()) || super.onOptionsItemSelected(item)
	}
}
