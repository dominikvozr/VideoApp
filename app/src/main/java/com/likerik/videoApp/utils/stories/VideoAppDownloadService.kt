package com.likerik.videoApp.utils.stories

import com.likerik.videoApp.utils.stories.DemoUtil.DOWNLOAD_NOTIFICATION_CHANNEL_ID

import android.R.drawable
import android.app.Notification
import android.content.Context
import androidx.annotation.Nullable
import com.likerik.videoApp.R
import com.likerik.videoApp.utils.stories.DemoUtil.getDownloadManager
import com.likerik.videoApp.utils.stories.DemoUtil.getDownloadNotificationHelper
import com.google.android.exoplayer2.ext.workmanager.WorkManagerScheduler
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.scheduler.Requirements
import com.google.android.exoplayer2.scheduler.Requirements.NETWORK
import com.google.android.exoplayer2.ui.DownloadNotificationHelper
import com.google.android.exoplayer2.util.NotificationUtil
import com.google.android.exoplayer2.util.Util


class VideoAppDownloadService() : DownloadService(
	FOREGROUND_NOTIFICATION_ID,
	DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
	DOWNLOAD_NOTIFICATION_CHANNEL_ID,
	R.string.exo_download_notification_channel_name,
	0
) {
	override fun getDownloadManager(): DownloadManager {
		val downloadManager = getDownloadManager(this)
		val downloadNotificationHelper = getDownloadNotificationHelper(this)
		downloadManager!!.addListener(
			TerminalStateNotificationHelper(
				this, downloadNotificationHelper!!, FOREGROUND_NOTIFICATION_ID + 1
			)
		)
		downloadManager.requirements = Requirements(NETWORK)
		return downloadManager
	}

	override fun getScheduler(): WorkManagerScheduler? {
		return WorkManagerScheduler(this, "work_name")
	}

	override fun getForegroundNotification(downloads: List<Download>): Notification {
		return getDownloadNotificationHelper(this)!!
			.buildProgressNotification(
				this,
				drawable.stat_sys_download,
				null,
				null,
				downloads
			)
	}

	/**
	 * Creates and displays notifications for downloads when they complete or fail.
	 *
	 *
	 * This helper will outlive the lifespan of a single instance of [VideoAppDownloadService].
	 * It is static to avoid leaking the first [VideoAppDownloadService] instance.
	 */
	private class TerminalStateNotificationHelper(
		context: Context, notificationHelper: DownloadNotificationHelper, firstNotificationId: Int
	) :
		DownloadManager.Listener {
		private val context: Context = context.applicationContext
		private val notificationHelper: DownloadNotificationHelper = notificationHelper
		private var nextNotificationId: Int = firstNotificationId

		override fun onDownloadChanged(
			downloadManager: DownloadManager,
			download: Download,
			@Nullable finalException: Exception?
		) {
			val utilString = Util.fromUtf8Bytes(download.request.data)
			val notification: Notification = if (download.state == Download.STATE_COMPLETED) {
				notificationHelper.buildDownloadCompletedNotification(
					context,
					drawable.stat_sys_download_done,
					null,
					utilString
				)
			} else if (download.state == Download.STATE_FAILED) {
				notificationHelper.buildDownloadFailedNotification(
					context,
					drawable.stat_sys_download_done,
					null,
					utilString
				)
			} else {
				return
			}
			NotificationUtil.setNotification(context, nextNotificationId++, notification)
		}

	}

	companion object {
		private const val FOREGROUND_NOTIFICATION_ID = 1
	}
}
