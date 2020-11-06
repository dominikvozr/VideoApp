package com.example.videoApp.utils
import android.content.Context
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.offline.ActionFileUpgradeUtil
import com.google.android.exoplayer2.offline.DefaultDownloadIndex
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.ui.DownloadNotificationHelper
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Log
import java.io.File
import java.io.IOException
import java.util.concurrent.Executors

object DemoUtil {
	const val DOWNLOAD_NOTIFICATION_CHANNEL_ID = "download_channel"
	private const val TAG = "DemoUtil"
	private const val DOWNLOAD_ACTION_FILE = "actions"
	private const val DOWNLOAD_TRACKER_ACTION_FILE = "tracked_actions"
	private const val DOWNLOAD_CONTENT_DIRECTORY = "downloads"


	private var httpDataSourceFactory: HttpDataSource.Factory? = null
	private var databaseProvider: DatabaseProvider? = null
	private var downloadDirectory: File? = null
	private var downloadCache: Cache? = null
	private var downloadManager: DownloadManager? = null
	private var downloadNotificationHelper: DownloadNotificationHelper? = null

	@Synchronized
	fun getHttpDataSourceFactory(): HttpDataSource.Factory? {
		if (httpDataSourceFactory == null) {
			httpDataSourceFactory = DefaultHttpDataSourceFactory()
		}
		return httpDataSourceFactory
	}

	@Synchronized
	fun getDownloadNotificationHelper(
		context: Context?
	): DownloadNotificationHelper? {
		if (downloadNotificationHelper == null) {
			downloadNotificationHelper =
				DownloadNotificationHelper(context!!, DOWNLOAD_NOTIFICATION_CHANNEL_ID)
		}
		return downloadNotificationHelper
	}

	@Synchronized
	fun getDownloadManager(context: Context): DownloadManager? {
		ensureDownloadManagerInitialized(context)
		return downloadManager
	}

	fun getDownloadCache(context: Context): Cache? {
		if (downloadCache == null) {
			val downloadContentDirectory =
				File(getDownloadDirectory(context), DOWNLOAD_CONTENT_DIRECTORY)
			downloadCache = SimpleCache(
				downloadContentDirectory, NoOpCacheEvictor(), getDatabaseProvider(context)!!
			)
		}
		return downloadCache
	}

	@Synchronized
	private fun ensureDownloadManagerInitialized(context: Context) {
		if (downloadManager == null) {
			val downloadIndex = DefaultDownloadIndex(getDatabaseProvider(context)!!)
			upgradeActionFile(
				context,
				DOWNLOAD_ACTION_FILE,
				downloadIndex,
				false
			)
			upgradeActionFile(
				context,
				DOWNLOAD_TRACKER_ACTION_FILE,
				downloadIndex,
				true
			)
			downloadManager = DownloadManager(
				context,
				getDatabaseProvider(context)!!,
				getDownloadCache(context)!!,
				getHttpDataSourceFactory()!!,
				Executors.newFixedThreadPool(6)
			)
		}
	}

	@Synchronized
	private fun upgradeActionFile(
		context: Context,
		fileName: String,
		downloadIndex: DefaultDownloadIndex,
		addNewDownloadsAsCompleted: Boolean
	) {
		try {
			ActionFileUpgradeUtil.upgradeAndDelete(
				File(getDownloadDirectory(context), fileName),
				null,
				downloadIndex,
				true,
				addNewDownloadsAsCompleted
			)
		} catch (e: IOException) {
			Log.e(
				TAG,
				"Failed to upgrade action file: $fileName", e
			)
		}
	}

	@Synchronized
	private fun getDatabaseProvider(context: Context): DatabaseProvider? {
		if (databaseProvider == null) {
			databaseProvider = ExoDatabaseProvider(context)
		}
		return databaseProvider
	}

	@Synchronized
	fun getDownloadDirectory(context: Context): File? {
		if (downloadDirectory == null) {
			downloadDirectory = context.getExternalFilesDir(null)
			if (downloadDirectory == null) {
				downloadDirectory = context.filesDir
			}
		}
		return downloadDirectory
	}
}
