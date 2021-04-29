package com.likerik.videoApp.domain

import android.net.Uri
import java.io.File

data class Video(val uri: Uri,
				 val file: File,
				 val name: String,
				 val duration: Int?,
				 val size: Int?)
