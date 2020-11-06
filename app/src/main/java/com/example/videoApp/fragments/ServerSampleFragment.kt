package com.example.videoApp.fragments

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.videoApp.R
import com.example.videoApp.databinding.FragmentServerSampleBinding
import com.example.videoApp.utils.serverPlayerUtils.HlsVideoPlayer
import com.example.videoApp.utils.serverPlayerUtils.ProgressiveVideoPlayer
import com.example.videoApp.utils.serverPlayerUtils.ServerVideoPlayer
import com.example.videoApp.viewModels.ServerSampleViewModel


class ServerSampleFragment : Fragment() {

	private lateinit var binding: FragmentServerSampleBinding

	private val viewModel: ServerSampleViewModel by lazy {
		ViewModelProviders.of(this).get(ServerSampleViewModel::class.java)
	}
	private var serverVideoPlayer: ServerVideoPlayer? = null

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
		setHasOptionsMenu(true)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		serverVideoPlayer?.let {
			it.onPlayerResume()
		}

		viewModel.dataPath.observe(this, Observer {
			sourceDecision(it)
		})
	}

	private fun sourceDecision(type: String) {
		when(type) {
			"HLS" -> {
				binding.hlsText.visibility = View.VISIBLE
				serverVideoPlayer = HlsVideoPlayer(binding, viewModel, context)
				serverVideoPlayer!!.setPlayer()
				binding.playerViewContainer.visibility = View.VISIBLE
				binding.buttonsContainer.visibility = View.GONE
			}
			"MP3" -> {
				binding.hlsText.visibility = View.GONE
				serverVideoPlayer = ProgressiveVideoPlayer(binding, viewModel, context, viewModel.mp3Path.value)
				serverVideoPlayer!!.setPlayer()
				binding.playerViewContainer.visibility = View.VISIBLE
				binding.buttonsContainer.visibility = View.GONE
			}
			"MP4" -> {
				binding.hlsText.visibility = View.GONE
				serverVideoPlayer = ProgressiveVideoPlayer(binding, viewModel, context, viewModel.mp4Path.value)
				serverVideoPlayer!!.setPlayer()
				binding.playerViewContainer.visibility = View.VISIBLE
				binding.buttonsContainer.visibility = View.GONE
			}
			"" -> {
				serverVideoPlayer!!.releasePlayer()
				binding.playerViewContainer.visibility = View.GONE
				binding.buttonsContainer.visibility = View.VISIBLE
			}
		}
	}
	override fun onStart() {
		super.onStart()
		serverVideoPlayer?.let {
			it.setPlayer()
		}
	}

	override fun onResume() {
		super.onResume()
		serverVideoPlayer?.let {
			it.setPlayWhenReady(true)
		}
	}

	override fun onPause() {
		super.onPause()
		serverVideoPlayer?.let {
			it.setPlayWhenReady(false)
		}
	}

	override fun onStop() {
		super.onStop()
		serverVideoPlayer?.let {
			it.setPlayWhenReady(false)
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		serverVideoPlayer?.let {
			it.releasePlayer()
		}
	}

	override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
		super.onCreateOptionsMenu(menu, inflater)
		inflater?.inflate(R.menu.menu,  menu)
	}

	override fun onOptionsItemSelected(item: MenuItem?): Boolean {
		return NavigationUI.onNavDestinationSelected(
			item!!,
			view!!.findNavController()) || super.onOptionsItemSelected(item)
	}
}
