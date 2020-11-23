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
import com.example.videoApp.databinding.FragmentHlsVideoPlayerBinding
import com.example.videoApp.utils.HlsAppPlayerViewAdapter
import com.example.videoApp.utils.ProgressiveAppPlayerViewAdapter
import com.example.videoApp.viewModels.HlsVideoPlayerViewModel

class HlsVideoPlayerFragment : Fragment() {

	private lateinit var binding: FragmentHlsVideoPlayerBinding

	private val viewModel: HlsVideoPlayerViewModel by lazy {
		ViewModelProviders.of(this).get(HlsVideoPlayerViewModel::class.java)
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		binding = DataBindingUtil.inflate(
			inflater,
			R.layout.fragment_hls_video_player,
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
				findNavController().navigate(HlsVideoPlayerFragmentDirections.actionHlsVideoPlayerFragmentToServerSampleFragment())
				HlsAppPlayerViewAdapter.releasePlayer()
			}
		}
		requireActivity().onBackPressedDispatcher.addCallback(this, callback)
	}

	override fun onResume() {
		super.onResume()
		HlsAppPlayerViewAdapter.resumePlayer()
	}

	override fun onPause() {
		super.onPause()
		HlsAppPlayerViewAdapter.pausePlayer()
	}

	override fun onStop() {
		super.onStop()
		HlsAppPlayerViewAdapter.pausePlayer()
	}

	override fun onDestroy() {
		super.onDestroy()
		HlsAppPlayerViewAdapter.releasePlayer()
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		super.onCreateOptionsMenu(menu, inflater)
		inflater.inflate(R.menu.menu,  menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return NavigationUI.onNavDestinationSelected(
			item,
			requireView().findNavController()) || super.onOptionsItemSelected(item)
	}
}
