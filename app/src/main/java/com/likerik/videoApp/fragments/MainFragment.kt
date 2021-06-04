package com.likerik.videoApp.fragments

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.likerik.videoApp.R
import com.likerik.videoApp.databinding.FragmentMainBinding
import com.likerik.videoApp.domain.Video
import com.likerik.videoApp.utils.main.PlayerViewAdapter
import com.likerik.videoApp.utils.RecyclerViewScrollListener
import com.likerik.videoApp.utils.main.VideoAdapter
import com.likerik.videoApp.viewModels.MainViewModel

class MainFragment : Fragment() {
	private var videoListAdapter : VideoAdapter? = null
	private lateinit var binding: FragmentMainBinding
	var last_id = -1;

    override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
		val model: MainViewModel by viewModels()
		binding.viewModel = model
		binding.lifecycleOwner = this
		setHasOptionsMenu(true)
        return binding.root
    }

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		//setAdapter()

		binding.viewModel?.gotVideos?.observe(viewLifecycleOwner, {
			if (it == true) {
				//videoListAdapter?.updateList(binding.viewModel?.videos as ArrayList<Video>)
				setAdapter()
			}
		})

		binding.videoButton.setOnClickListener {
			this.findNavController()
				.navigate(MainFragmentDirections.actionMainFragmentToCameraFragment())
			PlayerViewAdapter.releaseAllPlayers()
		}
	}

	private fun setAdapter() {
		videoListAdapter = VideoAdapter(requireContext(), binding.videoList, binding.viewModel?.videos as MutableList<Video>)

		//videoListAdapter!!.setHasStableIds(true)

		//binding.videoList.Recycler().setViewCacheSize(1)
		binding.videoList.adapter = videoListAdapter

		//binding.videoList.setHasFixedSize(true)

		val snapHelper: SnapHelper = PagerSnapHelper()
		snapHelper.attachToRecyclerView(binding.videoList)

		val videoListScrollListener = object : RecyclerViewScrollListener() {
			override fun onItemIsFirstVisibleItem(index: Int) {
				if(index != -1) {
					//if (last_id != -1) {
						//PlayerViewAdapter.releasePlayer(last_id - 1)
						//binding.videoList.layoutManager?.removeAndRecycleViewAt(last_id - 1, binding.videoList.Recycler())
					//}
					PlayerViewAdapter.playVideoOnIndex(index)
					//last_id = index
					//binding.videoList.recycledViewPool.clear()
				}
			}
		}

		binding.videoList.addOnScrollListener(videoListScrollListener)

		videoListAdapter?.setOnItemClickListener(object : VideoAdapter.OnItemClickListener {
			override fun onItemClick(view: View?, position: Int, video: Video?) {
				PlayerViewAdapter.toggleSound()
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
