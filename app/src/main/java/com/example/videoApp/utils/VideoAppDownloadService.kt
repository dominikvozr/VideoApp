package com.example.videoApp.utils

import android.R
import android.app.Notification
import android.os.Environment
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.scheduler.PlatformScheduler
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import java.util.concurrent.Executor


class VideoAppDownloadService() : DownloadService(
	FOREGROUND_NOTIFICATION_ID,
	DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
	"download_channel",
	0,   //channelDescriptionResourceId=
	0
) {
	private val downloadDirectory = "Downloads"
	override fun getDownloadManager(): DownloadManager {
		// Note: This should be a singleton in your app.
		val databaseProvider = ExoDatabaseProvider(this)

		// A download cache should not evict media, so should use a NoopCacheEvictor.
		val downloadCache = SimpleCache(
			Environment.getDownloadCacheDirectory(),
			NoOpCacheEvictor(),
			databaseProvider
		)
		// Create a factory for reading the data from the network.
		val dataSourceFactory = DefaultHttpDataSourceFactory()

		// Choose an executor for downloading data. Using Runnable::run will cause each download task to
		// download data on its own thread. Passing an executor that uses multiple threads will speed up
		// download tasks that can be split into smaller parts for parallel execution. Applications that
		// already have an executor for background downloads may wish to reuse their existing executor.
		val downloadExecutor = Executor { obj: Runnable -> obj.run() }

		// Create the download manager.
		val downloadManager = DownloadManager(
			this,
			databaseProvider,
			downloadCache,
			dataSourceFactory,
			downloadExecutor
		)

		// Optionally, setters can be called to configure the download manager.
		downloadManager.requirements = DownloadManager.DEFAULT_REQUIREMENTS
		downloadManager.maxParallelDownloads = 3

		return downloadManager
	}

	override fun getScheduler(): PlatformScheduler? {
		return if (Util.SDK_INT >= 21) PlatformScheduler(this, JOB_ID) else null
	}

	override fun getForegroundNotification(downloads: List<Download>): Notification {
		return DownloadServiceUtil.getDownloadNotificationHelper(this)!!
			.buildProgressNotification(
				this,
				R.drawable.stat_sys_download,
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
	/*private class TerminalStateNotificationHelper(
		context: Context, notificationHelper: DownloadNotificationHelper, firstNotificationId: Int
	) :
		DownloadManager.Listener {
		private val context: Context
		private val notificationHelper: DownloadNotificationHelper
		private var nextNotificationId: Int
		override fun onDownloadChanged(
			downloadManager: DownloadManager,
			download: Download,
			@Nullable finalException: Exception?
		) {
			val notification: Notification
			notification = if (download.state == Download.STATE_COMPLETED) {
				notificationHelper.buildDownloadCompletedNotification(
					context,
					R.drawable.stat_sys_download_done,  *//* contentIntent= *//*
					null,
					Util.fromUtf8Bytes(download.request.data)
				)
			} else if (download.state == Download.STATE_FAILED) {
				notificationHelper.buildDownloadFailedNotification(
					context,
					R.drawable.stat_sys_download_done,  *//* contentIntent= *//*
					null,
					Util.fromUtf8Bytes(download.request.data)
				)
			} else {
				return
			}
			NotificationUtil.setNotification(context, nextNotificationId++, notification)
		}

		init {
			this.context = context.getApplicationContext()
			this.notificationHelper = notificationHelper
			nextNotificationId = firstNotificationId
		}
	}*/

	companion object {
		private const val JOB_ID = 1
		private const val FOREGROUND_NOTIFICATION_ID = 1
	}
}
