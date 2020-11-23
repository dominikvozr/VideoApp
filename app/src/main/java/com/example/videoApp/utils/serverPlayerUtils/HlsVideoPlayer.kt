package com.example.videoApp.utils.serverPlayerUtils

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import com.example.videoApp.databinding.FragmentServerSampleBinding
import com.example.videoApp.utils.VideoAppDownloadService
import com.example.videoApp.viewModels.ServerSampleViewModel
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.offline.DownloadHelper
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.offline.StreamKey
import java.io.IOException

/*class HlsVideoPlayer(
	context: Context?,
	attrs: AttributeSet? = null
): ServerVideoPlayer( context!!, attrs!!) {
	override fun setPlayerMediaSource() {
		val downloadHelper = DownloadHelper.forMediaItem(
			context!!,
			MediaItem.fromUri(sourcePath.toUri()),
			DefaultRenderersFactory(context!!),
			cacheDataSourceFactory
		)
		downloadHelper.prepare(DownloadHelperCallback())
	}

	private inner class DownloadHelperCallback() : DownloadHelper.Callback {
		override fun onPrepared(helper: DownloadHelper) {
			val trackList = arrayListOf<StreamKey>()
			for (i in 0 until helper.periodCount) {
				val trackGroups = helper.getTrackGroups(i)
				for (j in 0 until trackGroups.length) {
					val trackGroup = trackGroups.get(j)
					for (k in 0 until trackGroup.length) {
						val track = trackGroup.getFormat(k)
						if(shouldDownload(track)) {
							trackList.add(StreamKey(i, j, k))
						}

					}
				}
			}


			var downloadRequest = helper.getDownloadRequest(ByteArray(trackList.size))

			val mediaItem = downloadRequest.toMediaItem()

			player.setMediaItem(mediaItem)
			if (trackList.isNotEmpty()) {
				DownloadService.sendAddDownload(
					context!!,
					VideoAppDownloadService::class.java,
					downloadRequest,
					false
				)
			}
			helper.release()
		}

		private fun shouldDownload(track: Format): Boolean {
			return track.height != 240 && track.sampleMimeType.equals("video/avc", true);
		}


		override fun onPrepareError(helper: DownloadHelper, e: IOException) {
			Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show()
			Log.e("ServerSampleFragment", e.toString())
		}
	}
}*/
