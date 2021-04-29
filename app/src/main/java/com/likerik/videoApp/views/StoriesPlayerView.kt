package com.likerik.videoApp.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.likerik.videoApp.databinding.StoriesPlayerViewBinding
import com.likerik.videoApp.domain.HlsPlay
import com.likerik.videoApp.domain.NewStoriesVideo
import com.likerik.videoApp.utils.stories.StoriesPlayerViewAdapter
import com.google.android.exoplayer2.SimpleExoPlayer


class StoriesPlayerView : ConstraintLayout {
	lateinit var binding : StoriesPlayerViewBinding
	lateinit var playlist: NewStoriesVideo

	private var landingIndex = 0

	@JvmOverloads
	constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
			: super(context, attrs, defStyleAttr) {
		init()
	}

	private fun init() {
		val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
		binding = StoriesPlayerViewBinding.inflate(inflater, this, true)
		/*next_btn.setOnClickListener {
			playerView.player?.let{ player ->
				if (playlist.landing.size-1 > landingIndex)
					player.seekTo((playlist.landing[landingIndex+1].storiesVideo.track_offset*1000).toLong())
			}
		}

		prev_btn.setOnClickListener {
			playerView.player?.let{ player ->
				if (landingIndex > 0)
					player.seekTo((playlist.landing[landingIndex-1].storiesVideo.track_offset*1000).toLong())
			}
		}*/
	}

	fun setupTitles(hlsPlayer: SimpleExoPlayer) {
		binding.textViewMusicTitle.text = StoriesPlayerViewAdapter.playlist!!.id.toString()
		//this.video_id.text = StoriesPlayerViewAdapter.playlist!!.id.toString()
		/*for (landingI in StoriesPlayerViewAdapter.playlist!!.landing.indices) {
			val offset = StoriesPlayerViewAdapter.playlist!!.landing[landingI].storiesVideo.track_offset * 1000
			hlsPlayer.createMessage { _: Int, _: Any? -> videoChanged(
				StoriesPlayerViewAdapter.playlist!!.landing[landingI],
				landingI
			) }
				.setPosition(offset.toLong()).setDeleteAfterDelivery(false).send()
		}*/
	}

	private fun videoChanged(landing: HlsPlay, landingI: Int) {
		landingIndex = landingI
		post{
			/*text_view_account_handle.text = landing.user.getStoryDisplayUserName()
			text_view_video_description.text = landing.storiesVideo.getTagsDescription()
			text_view_video_location.text = landing.storiesVideo.getLocation()*/
		}
	}
}
