package com.likerik.videoApp.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.likerik.videoApp.R
import com.likerik.videoApp.utils.camera.CameraListener
import com.likerik.videoApp.viewModels.CameraViewModel
import com.likerik.videoApp.databinding.FragmentCameraBinding
import com.otaliastudios.cameraview.controls.Facing

class CameraFragment : Fragment() {

	private lateinit var binding: FragmentCameraBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
		// data transaction
        binding = FragmentCameraBinding.inflate(inflater)

		val cameraViewModel: CameraViewModel by viewModels()

		binding.cameraViewModel = cameraViewModel

		binding.lifecycleOwner = this

		// camera setting up
		if (context?.packageManager!!.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
			// this device has a camera
			val cameraListener = CameraListener(cameraViewModel)
			binding.camera.addCameraListener(cameraListener)
			binding.camera.mode = cameraViewModel.cameraMode
			binding.camera.open()
		}
		binding.camera

		//observer
		cameraViewModel.onTakeVideo.observe(viewLifecycleOwner, {
			when (it) {
				true  -> {
					cameraViewModel.video?.file?.let { videoFile ->
						binding.camera.takeVideo(videoFile)
					}
				}
				false -> {
					if (binding.camera.isTakingVideo) {
						binding.camera.stopVideo()
					}
				}
			}
		})

		cameraViewModel.videoResult.observe(viewLifecycleOwner, {
			if (null != it) {
				this.findNavController().navigate(
                    CameraFragmentDirections.actionCameraFragmentToVideoPlayerFragment(
                        it.file.path
                    )
                )
				cameraViewModel.navigationCompleted()
			}
		})

		cameraViewModel.onChangeCamera.observe(viewLifecycleOwner, {
			if (true == it) {
				if (binding.camera.facing == Facing.FRONT) {
					binding.camera.facing = Facing.BACK
				} else {
					binding.camera.facing = Facing.FRONT
				}
				cameraViewModel.cameraChanged()
			}
		})

		setHasOptionsMenu(true)
		return binding.root
	}


	override fun onDestroyView() {
		binding.camera.close()
		super.onDestroyView()
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		super.onCreateOptionsMenu(menu, inflater)
		inflater.inflate(R.menu.menu,  menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return NavigationUI.onNavDestinationSelected(
			item,
			requireView().findNavController()) || super.onOptionsItemSelected(item)
	}
}
