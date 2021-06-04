package com.likerik.videoApp.domain

data class StoriesVideo (
	val chainId : String,
	val duration: Double,
	val guildId : String,
	val timestamp: String?,
	val track_offset: Double,
	val video_create_date : String?,
	val video_description : String,
	val video_last_frame_url : String,
	val video_location: Location,
	var video_tags: List<String>?,
	val video_thumbnail_url : String,
	val video_url : String,
	val _key : String
		) {
	fun setVideoTags ( videoTags: List<String> ) {
		this.video_tags = videoTags
	}

	fun getTagsDescription() : String {
		var tags = ""
		video_tags?.forEach {
			tags += " #$it"
		}
		return video_description + tags
	}

	fun getLocation() : String {
		return "${video_location.city}, ${video_location.country}"
	}
}

data class NewStoriesVideo (
	val id 		           : Int,
	val quality            : String,
	val file_type          : String,
	val width	           : Int,
	val height	           : Int,
	val link_screenshot    : String,
	val link 	           : String,
	val link_480p          : String,
	val link_480p_tinyfied : String,
	val m3u8               : String,
)

interface VideoBuilder {
	fun withOffset(offset: Double) : VideoBuilder
	fun withVideoTags(tags: List<String>) : VideoBuilder
	fun withVideoLocation(location: Location) : VideoBuilder
	fun withVideoUrl(videoUrl: String) : VideoBuilder
	fun withVideoThumbnailUrl(videoThumbnailUrl: String) : VideoBuilder
	fun withDuration(duration: Double) : VideoBuilder
	fun withGuildId(guildId: String) : VideoBuilder
	fun withKey(key: String) : VideoBuilder
	fun withTimestamp(timestamp: String) : VideoBuilder
	fun withVideoLastFrameUrl(videoLastFrameUrl: String) : VideoBuilder
	fun withChainId(chainId: String) : VideoBuilder
	fun withVideoDescription(videoDescription: String) : VideoBuilder
	fun withVideoCreateDate(videoCreateDate: String) : VideoBuilder

	fun build() : StoriesVideo
}

class ConcreteVideoBuilder: VideoBuilder {
	private var trackOffset: 		Double? 			= null
	private var videoTags: 			List<String>? 	= null
	private var videoLocation: 		Location? 		= null
	private var videoUrl : 			String? 		= null
	private var videoThumbnailUrl : String? 		= null
	private var duration: 			Double? 			= null
	private var guildId : 			String? 		= null
	private var _key : 				String? 		= null
	private var timestamp: 			String? 		= null
	private var videoLastFrameUrl : String? 		= null
	private var chainId : 			String? 		= null
	private var videoDescription : 	String? 		= null
	private var videoCreateDate : 	String? 			= null

	override fun withOffset(offset: Double): VideoBuilder {
		this.trackOffset = offset
		return this
	}

	override fun withVideoTags(tags: List<String>): VideoBuilder {
		this.videoTags = tags
		return this
	}

	override fun withVideoLocation(location: Location): VideoBuilder {
		this.videoLocation = location
		return this
	}

	override fun withVideoUrl(videoUrl: String): VideoBuilder {
		this.videoUrl = videoUrl
		return this
	}

	override fun withVideoThumbnailUrl(videoThumbnailUrl: String): VideoBuilder {
		this.videoThumbnailUrl = videoThumbnailUrl
		return this
	}

	override fun withDuration(duration: Double): VideoBuilder {
		this.duration = duration
		return this
	}

	override fun withGuildId(guildId: String): VideoBuilder {
		this.guildId = guildId
		return this
	}

	override fun withKey(key: String): VideoBuilder {
		this._key = key
		return this
	}

	override fun withTimestamp(timestamp: String): VideoBuilder {
		this.timestamp = timestamp
		return this
	}

	override fun withVideoLastFrameUrl(videoLastFrameUrl: String): VideoBuilder {
		this.videoLastFrameUrl = videoLastFrameUrl
		return this
	}

	override fun withChainId(chainId: String): VideoBuilder {
		this.chainId = chainId
		return this
	}

	override fun withVideoDescription(videoDescription: String): VideoBuilder {
		this.videoDescription = videoDescription
		return this
	}

	override fun withVideoCreateDate(videoCreateDate: String): VideoBuilder {
		this.videoCreateDate = videoCreateDate
		return this
	}


	override fun build(): StoriesVideo {
		return StoriesVideo(chainId!!, duration!!, guildId!!, timestamp, trackOffset!!, videoCreateDate!!, videoDescription!!, videoLastFrameUrl!!, videoLocation!!, videoTags!!, videoThumbnailUrl!!, videoUrl!!, _key!!)
	}
}
