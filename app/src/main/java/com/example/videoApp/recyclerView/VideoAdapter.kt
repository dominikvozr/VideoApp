package com.example.videoApp.recyclerView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.videoApp.domain.Video
import com.example.videoApp.databinding.ListItemVideoBinding
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer

class VideoAdapter(private val context: Context, private var videos: ArrayList<Video>) : RecyclerView.Adapter<VideoAdapter.ViewHolder>(),
	PlayerStateCallback {

	var videoClickListener: OnItemClickListener? = null

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val item = getItem(position)
		holder.bind(item)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val layoutInflater = LayoutInflater.from(parent.context)
		val binding = ListItemVideoBinding.inflate(layoutInflater, parent, false)
		return ViewHolder(binding)
	}

	override fun getItemCount(): Int {
		return videos.count()
	}

	override fun onViewRecycled(holder: ViewHolder) {
		val position = holder.adapterPosition
		PlayerViewAdapter.releasePlayer(position)
		super.onViewRecycled(holder)
	}

	fun setOnItemClickListener(mItemClickListener: OnItemClickListener?) {
		this.videoClickListener = mItemClickListener
	}

	interface OnItemClickListener {
		fun onItemClick(
			view: View?,
			position: Int,
			video: Video?
		)
	}

	inner class ViewHolder constructor(private val binding: ListItemVideoBinding) : RecyclerView.ViewHolder(binding.root) {

		fun bind(video: Video) {
			binding.root.setOnClickListener {
				videoClickListener!!.onItemClick(it, adapterPosition, video)
			}
			binding.video = video
			binding.callback = this@VideoAdapter
			binding.index = adapterPosition
			binding.executePendingBindings()
		}
	}

	private fun getItem(position: Int): Video {
		return videos[position]
	}

	fun updateList(videos: ArrayList<Video>) {
		this.videos = videos
	}

	override fun onVideoDurationRetrieved(duration: Long, player: Player) {}

	override fun onVideoBuffering(player: Player) {}

	override fun onStartedPlaying(player: Player) {}

	override fun onFinishedPlaying(player: Player) {}
}

