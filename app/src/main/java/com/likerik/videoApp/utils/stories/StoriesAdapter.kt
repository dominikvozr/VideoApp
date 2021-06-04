package com.likerik.videoApp.utils.stories

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.likerik.videoApp.databinding.StoriesListItemVideoBinding
import com.likerik.videoApp.domain.NewStoriesVideo
import com.likerik.videoApp.utils.PlayerStateCallback
import com.google.android.exoplayer2.Player


class StoriesAdapter(
	private val context: Context,
	private val recyclerView: RecyclerView,
	private var videos: MutableList<NewStoriesVideo>
) : RecyclerView.Adapter<StoriesAdapter.ViewHolder>(),
    PlayerStateCallback {

	var videoClickListener: OnItemClickListener? = null

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val item = getItem(position)
		holder.bind(item)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val layoutInflater = LayoutInflater.from(parent.context)
		val binding = StoriesListItemVideoBinding.inflate(layoutInflater, parent, false)
		return ViewHolder(binding)
	}

	override fun getItemCount(): Int {
		return videos.count()
	}

	override fun getItemId(position: Int): Long {
		return getItem(position).id.hashCode().toLong() //updated
	}

	override fun onViewRecycled(holder: ViewHolder) {
		val position = holder.adapterPosition
        StoriesPlayerViewAdapter.releasePlayer(position)
		super.onViewRecycled(holder)
	}

	fun setOnItemClickListener(mItemClickListener: OnItemClickListener?) {
		this.videoClickListener = mItemClickListener
	}

	interface OnItemClickListener {
		fun onItemClick(
			view: View?,
			position: Int,
			newStoriesVideo: NewStoriesVideo?
		)
	}

	inner class ViewHolder constructor(private val binding: StoriesListItemVideoBinding) : RecyclerView.ViewHolder(
		binding.root
	) {
		fun bind(newStoriesVideolist: NewStoriesVideo) {
			binding.root.setOnClickListener {
				videoClickListener!!.onItemClick(it, adapterPosition, newStoriesVideolist)
			}
			binding.newStoriesVideo = newStoriesVideolist
			binding.callback = this@StoriesAdapter
			binding.index = adapterPosition
			binding.executePendingBindings()
		}
	}

	private fun getItem(position: Int): NewStoriesVideo {
		return videos[position]
	}

	override fun onVideoDurationRetrieved(duration: Long, player: Player) {}

	override fun onVideoBuffering(player: Player) {}

	override fun onStartedPlaying(player: Player) {}

	@ExperimentalStdlibApi
	override fun onFinishedPlaying(player: Player, item_index: Int) {
		var indx = item_index + 1
		if (indx + 1 <= this.videos.size) {
			this.recyclerView.smoothScrollToPosition(item_index + 1)
		}
	}

}

