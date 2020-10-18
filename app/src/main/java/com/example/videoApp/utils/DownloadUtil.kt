package com.example.videoApp.utils

/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import android.content.Context
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.DefaultRenderersFactory.ExtensionRendererMode
import com.google.android.exoplayer2.RenderersFactory
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.ExoDatabaseProvider
//import com.google.android.exoplayer2.ext.cronet.CronetDataSourceFactory
//import com.google.android.exoplayer2.ext.cronet.CronetEngineWrapper
import com.google.android.exoplayer2.offline.ActionFileUpgradeUtil
import com.google.android.exoplayer2.offline.DefaultDownloadIndex
//import com.google.android.exoplayer2.offline.DownloadHelper
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.ui.DownloadNotificationHelper
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Log
//import org.checkerframework.checker.nullness.qual.MonotonicNonNull
import java.io.File
import java.io.IOException
import java.util.concurrent.Executors


/** Utility methods for the demo app.  */
/*object DownloadServiceUtil {
	private const val DOWNLOAD_NOTIFICATION_CHANNEL_ID = "download_channel"
	private var downloadNotificationHelper: DownloadNotificationHelper? = null

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
}*/


/** Utility methods for the demo app.  */
object DemoUtil {
	const val DOWNLOAD_NOTIFICATION_CHANNEL_ID = "download_channel"
	private const val TAG = "DemoUtil"
	private const val DOWNLOAD_ACTION_FILE = "actions"
	private const val DOWNLOAD_TRACKER_ACTION_FILE = "tracked_actions"
	private const val DOWNLOAD_CONTENT_DIRECTORY = "downloads"


	private var dataSourceFactory: DataSource.Factory? = null
	private var httpDataSourceFactory: HttpDataSource.Factory? = null

	private var databaseProvider: DatabaseProvider? = null

	private var downloadDirectory: File? = null

	private var downloadCache: Cache? = null

	private var downloadManager: DownloadManager? = null

	//private var downloadTracker: DownloadTracker? = null

	private var downloadNotificationHelper: DownloadNotificationHelper? = null

	/** Returns whether extension renderers should be used.  */
	fun useExtensionRenderers(): Boolean {
		//return BuildConfig.USE_DECODER_EXTENSIONS
		return true
	}

	fun buildRenderersFactory(
		context: Context, preferExtensionRenderer: Boolean
	): RenderersFactory {
		@ExtensionRendererMode val extensionRendererMode =
			if (useExtensionRenderers())
				if (preferExtensionRenderer)
					DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
				else
					DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON
			else
				DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF
		return DefaultRenderersFactory(context.applicationContext)
			.setExtensionRendererMode(extensionRendererMode)
	}

	@Synchronized
	fun getHttpDataSourceFactory(context: Context): HttpDataSource.Factory? {
		var context = context
		if (httpDataSourceFactory == null) {
			context = context.applicationContext
			//val cronetEngineWrapper = CronetEngineWrapper(context)
			httpDataSourceFactory = DefaultHttpDataSourceFactory()//CronetDataSourceFactory(cronetEngineWrapper, Executors.newSingleThreadExecutor())
		}
		return httpDataSourceFactory
	}

	/** Returns a [DataSource.Factory].  */
	@Synchronized
	fun getDataSourceFactory(context: Context): DataSource.Factory? {
		var context = context
		if (dataSourceFactory == null) {
			context = context.applicationContext
			val upstreamFactory = DefaultDataSourceFactory(
				context,
				getHttpDataSourceFactory(context)!!
			)
			dataSourceFactory =
				buildReadOnlyCacheDataSource(upstreamFactory, getDownloadCache(context))
		}
		return dataSourceFactory
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

	@Synchronized
	/*fun getDownloadTracker(context: Context): DownloadTracker? {
		ensureDownloadManagerInitialized(context)
		return downloadTracker
	}*/

	//@Synchronized
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
				getHttpDataSourceFactory(context)!!,
				Executors.newFixedThreadPool( /* nThreads= */6)
			)
			/*downloadTracker =
				DownloadTracker(context, getHttpDataSourceFactory(context), downloadManager)*/
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
				File(getDownloadDirectory(context), fileName),  /* downloadIdProvider= */
				null,
				downloadIndex,  /* deleteOnFailure= */
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
			downloadDirectory = context.getExternalFilesDir( /* type= */null)
			if (downloadDirectory == null) {
				downloadDirectory = context.filesDir
			}
		}
		return downloadDirectory
	}

	private fun buildReadOnlyCacheDataSource(
		upstreamFactory: DataSource.Factory, cache: Cache?
	): CacheDataSource.Factory {
		return CacheDataSource.Factory()
			.setCache(cache!!)
			.setUpstreamDataSourceFactory(upstreamFactory)
			.setCacheWriteDataSinkFactory(null)
			.setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
	}
}
