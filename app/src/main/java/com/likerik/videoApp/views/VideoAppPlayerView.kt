package com.likerik.videoApp.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.likerik.videoApp.databinding.VideoAppPlayerViewBinding
import com.google.android.exoplayer2.SimpleExoPlayer

class VideoAppPlayerView : ConstraintLayout {
	lateinit var binding : VideoAppPlayerViewBinding

	@JvmOverloads
	constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
			: super(context, attrs, defStyleAttr) {
		init()
	}

	fun init() {
		val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
		binding = VideoAppPlayerViewBinding.inflate(inflater, this, true)
	}
}
