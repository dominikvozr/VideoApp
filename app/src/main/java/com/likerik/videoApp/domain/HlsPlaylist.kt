package com.likerik.videoApp.domain

data class HlsPlaylist (
	val playlistURL: String,
	val landing: List<HlsPlay>,
	val track: Track
) {
	constructor() : this("", listOf(), Track())
}

data class HlsPlay (
	val storiesVideo : StoriesVideo,
	val user : User,
	val isCheered : Boolean
)

data class Track (
	val max_segment_count : Short,
	val track_author : String,
	val track_duration : Float,
	val track_tags : List<String>,
	val track_title : String,
	val track_url : String,
	val _key : String
) {
	constructor() : this(0, "", 0f, listOf(), "", "", "")
	fun getTrackName() : String {
		return "$track_title - $track_author"
	}
}
