package com.example.videoApp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.example.videoApp.R
import com.example.videoApp.databinding.FragmentMainBinding
import com.example.videoApp.domain.Video
import com.example.videoApp.recyclerView.PlayerViewAdapter
import com.example.videoApp.recyclerView.RecyclerViewScrollListener
import com.example.videoApp.recyclerView.VideoAdapter
import com.example.videoApp.viewModels.MainViewModel

class MainFragment : Fragment() {
	private var videoListAdapter : VideoAdapter? = null
	private lateinit var binding: FragmentMainBinding

	private val mainViewModel: MainViewModel by lazy {
		ViewModelProviders.of(this).get(MainViewModel::class.java)
	}

    override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
		binding.viewModel = mainViewModel
		binding.lifecycleOwner = this

        return binding.root
    }

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		setAdapter()

		mainViewModel.gotVideos.observe(viewLifecycleOwner, Observer {
			if (it == true) {
				videoListAdapter?.updateList(mainViewModel.videos as ArrayList<Video>)
			}
		})

		binding.videoButton.setOnClickListener {
			this.findNavController()
				.navigate(MainFragmentDirections.actionMainFragmentToCameraFragment())
			PlayerViewAdapter.releaseAllPlayers()
		}
	}

	private fun setAdapter() {
		videoListAdapter = VideoAdapter(context!!, mainViewModel.videos as ArrayList<Video>)

		binding.videoList.adapter = videoListAdapter

		val snapHelper: SnapHelper = LinearSnapHelper()
		snapHelper.attachToRecyclerView(binding.videoList)

		val videoListScrollListener = object : RecyclerViewScrollListener() {
			override fun onItemIsFirstVisibleItem(index: Int) {
				if(index != -1) {
					PlayerViewAdapter.playVideoOnIndex(index)
				}
			}
		}

		binding.videoList.addOnScrollListener(videoListScrollListener)

		videoListAdapter?.setOnItemClickListener(object : VideoAdapter.OnItemClickListener {
			override fun onItemClick(view: View?, position: Int, video: Video?) {
				PlayerViewAdapter.toggleSound(position)
			}
		})
	}

	override fun onPause() {
		super.onPause()
		PlayerViewAdapter.releaseAllPlayers()
	}
	override fun onStop() {
		super.onStop()
		PlayerViewAdapter.releaseAllPlayers()
	}

}