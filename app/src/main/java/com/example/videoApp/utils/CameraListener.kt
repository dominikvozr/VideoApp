package com.example.videoApp.utils

import android.util.Log
import com.example.videoApp.viewModels.CameraViewModel
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraOptions
import com.otaliastudios.cameraview.VideoResult

class CameraListener(
	private val cameraViewModel: CameraViewModel
) : CameraListener() {

	override fun onCameraOpened(options: CameraOptions) {
		super.onCameraOpened(options)
		Log.i("CameraListener", "Camera opened")
	}

	override fun onCameraClosed() {
		super.onCameraClosed()
		Log.i("CameraListener", "Camera closed")
	}

	override fun onVideoRecordingStart() {
		// Notifies that the actual video recording has started.
		// Can be used to show some UI indicator for video recording or counting time.
	}

	override fun onVideoRecordingEnd() {
		// Notifies that the actual video recording has ended.
		// Can be used to remove UI indicators added in onVideoRecordingStart.
	}

	override fun onVideoTaken(result: VideoResult) {
		cameraViewModel.setVideoResult(result)
		super.onVideoTaken(result)
	}
}
