package com.example.videoApp.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.videoApp.utils.CameraListener
import com.example.videoApp.viewModels.CameraViewModel
import com.example.videoApp.databinding.FragmentCameraBinding
import com.otaliastudios.cameraview.controls.Facing
import kotlinx.android.synthetic.main.fragment_camera.*
private val TAG = "CameraFragment"

class CameraFragment : Fragment() {

	private val cameraViewModel: CameraViewModel by lazy {
		//val activity = requireNotNull(this.activity){}
		ViewModelProviders.of(this).get(CameraViewModel::class.java)
	}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
		// data transaction
        val binding = FragmentCameraBinding.inflate(inflater)

		binding.cameraViewModel = cameraViewModel

		binding.lifecycleOwner = this

		// camera setting up
		if (context?.packageManager!!.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			val cameraListener = CameraListener(cameraViewModel)
			binding.camera.addCameraListener(cameraListener)
			binding.camera.mode = cameraViewModel.cameraMode
			binding.camera.open()
		}
		binding.camera

		//observer
		cameraViewModel.onTakeVideo.observe(this, Observer {
			when (it) {
				true  -> {
					cameraViewModel.video?.file?.let { videoFile ->
						camera.takeVideo(videoFile)
					}
				}
				false -> {
					if (camera.isTakingVideo) {
						camera.stopVideo()
					}
				}
			}
		})

		cameraViewModel.videoResult.observe(this, Observer {
			if (null != it) {
				this.findNavController().navigate(
                    CameraFragmentDirections.actionCameraFragmentToVideoPlayerFragment(
                        it.file.path
                    )
                )
				cameraViewModel.navigationCompleted()
			}
		})

		cameraViewModel.onChangeCamera.observe(this, Observer {
			if (true == it) {
				if (camera.facing == Facing.FRONT) {
					camera.facing = Facing.BACK
				} else {
					camera.facing = Facing.FRONT
				}
				cameraViewModel.cameraChanged()
			}
		})


		return binding.root
	}


	override fun onDestroyView() {
		camera.close()
		super.onDestroyView()
	}
}
