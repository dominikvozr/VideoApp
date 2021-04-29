package com.likerik.videoApp.fragments

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.likerik.videoApp.R
import com.likerik.videoApp.databinding.FragmentStoriesBinding
import com.likerik.videoApp.domain.NewStoriesVideo
import com.likerik.videoApp.utils.RecyclerViewScrollListener
import com.likerik.videoApp.utils.stories.StoriesPlayerViewAdapter
import com.likerik.videoApp.viewModels.StoriesViewModel
import com.likerik.videoApp.utils.stories.StoriesAdapter

class StoriesFragment : Fragment() {
	private var videoListAdapter : StoriesAdapter? = null
	private lateinit var binding: FragmentStoriesBinding
	private val viewModel: StoriesViewModel by viewModels()

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_stories, container, false)
		binding.lifecycleOwner = this

        return binding.root
    }

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		viewModel.newStoriesVideo.observe(viewLifecycleOwner, {
			if (it.isNotEmpty()) {
				setAdapter()
			}
		})
	}

	private fun setAdapter() {
		videoListAdapter = StoriesAdapter(requireContext(), binding.videoList,
			viewModel.newStoriesVideo.value!!.toList() as MutableList<NewStoriesVideo>
		)
		videoListAdapter!!.setHasStableIds(true)

		binding.videoList.adapter = videoListAdapter

		binding.videoList.setHasFixedSize(true)

		/*val displayMetrics = DisplayMetrics()
		activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics);

		var height = displayMetrics.heightPixels
		//binding.videoList.setItemViewCacheSize(20)
		binding.videoList.layoutManager = context?.let { PreCachingLayoutManager(it, height * 2) }*/
		//binding.videoList.layoutManager?.isItemPrefetchEnabled

		val snapHelper: SnapHelper = PagerSnapHelper()
		snapHelper.attachToRecyclerView(binding.videoList)



		val videoListScrollListener = object : RecyclerViewScrollListener() {
			override fun onItemIsFirstVisibleItem(index: Int) {
				if(index != -1) {
					//StoriesPlayerViewAdapter.currentPlayingVideo.second.pause()
					//StoriesPlayerViewAdapter.currentPlayingVideo.let {
						if ( StoriesPlayerViewAdapter.currentPlayingVideo?.first != index)
							StoriesPlayerViewAdapter.playVideoOnIndex(index)
					}
				//}
			}
		}

		binding.videoList.addOnScrollListener(videoListScrollListener)

		videoListAdapter?.setOnItemClickListener(object : StoriesAdapter.OnItemClickListener {
			override fun onItemClick(view: View?, position: Int, newStoriesVideo: NewStoriesVideo?) {
				StoriesPlayerViewAdapter.toggleSound()
			}
		})
	}
	override fun onStart() {
		super.onStart()
		viewModel.getStories()
	}




	override fun onResume() {
		binding.videoList.onFlingListener = null;
		super.onResume()
	}

	override fun onPause() {
		super.onPause()
	}

	/**
	 * Called when the Fragment is no longer started.  This is generally
	 * tied to [Activity.onStop] of the containing
	 * Activity's lifecycle.
	 */
	override fun onStop() {
		StoriesPlayerViewAdapter.releaseAllPlayers()
		super.onStop()
	}

	override fun onDestroy() {
		super.onDestroy()
	}

	/*override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		super.onCreateOptionsMenu(menu, inflater)
		inflater.inflate(R.menu.menu,  menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return NavigationUI.onNavDestinationSelected(
			item,
			requireView().findNavController()) || super.onOptionsItemSelected(item)
	}*/
}
