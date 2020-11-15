package com.example.videoApp.utils.serverPlayerUtils

import android.content.Context
import android.util.AttributeSet
import androidx.core.net.toUri
import com.example.videoApp.databinding.FragmentServerSampleBinding
import com.example.videoApp.utils.DemoUtil
import com.example.videoApp.utils.VideoAppDownloadService
import com.example.videoApp.viewModels.ServerSampleViewModel
import com.google.android.exoplayer2.offline.DownloadRequest
import com.google.android.exoplayer2.offline.DownloadService

class ProgressiveVideoPlayer (
	/*binding: FragmentServerSampleBinding,
	viewModel: ServerSampleViewModel,*/
	context: Context?,
	private val strSource: String?,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
): ServerVideoPlayer( strSource!!, context!!, attrs, defStyleAttr) {
	override fun setPlayerMediaSource() {
		val downloadRequest = DownloadRequest.Builder(
			strSource!!,
			strSource!!.toUri()
		).build()
		player.setMediaItem(downloadRequest.toMediaItem())
		DownloadService.sendAddDownload(
			context!!,
			VideoAppDownloadService::class.java,
			downloadRequest,
			false
		)
	}
}
