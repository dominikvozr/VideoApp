package com.likerik.videoApp.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.likerik.videoApp.databinding.StoriesPlayerViewBinding
import com.likerik.videoApp.domain.NewStoriesVideo
import com.likerik.videoApp.utils.stories.StoriesPlayerViewAdapter
import com.google.android.exoplayer2.SimpleExoPlayer


class StoriesPlayerView : ConstraintLayout {
	lateinit var binding : StoriesPlayerViewBinding
	lateinit var playlist: NewStoriesVideo
	private val STEP : Long = 2000

	@JvmOverloads
	constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
			: super(context, attrs, defStyleAttr) {
		init()
	}

	private fun init() {
		val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
		binding = StoriesPlayerViewBinding.inflate(inflater, this, true)
		binding.nextBtn.setOnClickListener {
			binding.playerView.player?.let{ player ->
				if (player.contentPosition + STEP < player.duration)
					player.seekTo(player.contentPosition + STEP)
				else {
					StoriesPlayerViewAdapter
						.playerStateCallback
						?.onFinishedPlaying(
							StoriesPlayerViewAdapter.hlsPlayer!!,
							StoriesPlayerViewAdapter.currentPlayingVideo!!.first
						)
				}
			}
		}

		binding.prevBtn.setOnClickListener {
			binding.playerView.player?.let{ player ->
				if (player.contentPosition - STEP > 0)
					player.seekTo(player.contentPosition - STEP)
				else
					player.seekTo(0)
			}
		}

		binding.middle.setOnClickListener {
			StoriesPlayerViewAdapter.toggleSound()
		}
	}

	fun setupTitles(hlsPlayer: SimpleExoPlayer) {
		//binding.textViewMusicTitle.text = StoriesPlayerViewAdapter.playlist!!.id.toString()
		for (landingI in 0..StoriesPlayerViewAdapter.hlsPlayer!!.duration step(STEP) ) {
			hlsPlayer.createMessage { _: Int, _: Any? -> videoChanged(
					landingI
			) }
					.setPosition(landingI).setDeleteAfterDelivery(false).send()
		}
	}

	private fun videoChanged(landingI: Long) {
		val musicTitleText = StoriesPlayerViewAdapter.playlist!!.id.toString() + " - " + landingI.toString()
		post{
			binding.textViewMusicTitle.text = musicTitleText
			/*text_view_account_handle.text = landing.user.getStoryDisplayUserName()
			text_view_video_description.text = landing.storiesVideo.getTagsDescription()
			text_view_video_location.text = landing.storiesVideo.getLocation()*/
		}
	}
}
