package com.example.videoApp.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.videoApp.R
import com.example.videoApp.utils.CameraListener
import com.example.videoApp.viewModels.CameraViewModel
import com.example.videoApp.databinding.FragmentCameraBinding
import com.otaliastudios.cameraview.controls.Facing
import kotlinx.android.synthetic.main.fragment_camera.*

class CameraFragment : Fragment() {

	private val cameraViewModel: CameraViewModel by lazy {

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
		cameraViewModel.onTakeVideo.observe(viewLifecycleOwner, Observer {
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

		cameraViewModel.videoResult.observe(viewLifecycleOwner, Observer {
			if (null != it) {
				this.findNavController().navigate(
                    CameraFragmentDirections.actionCameraFragmentToVideoPlayerFragment(
                        it.file.path
                    )
                )
				cameraViewModel.navigationCompleted()
			}
		})

		cameraViewModel.onChangeCamera.observe(viewLifecycleOwner, Observer {
			if (true == it) {
				if (camera.facing == Facing.FRONT) {
					camera.facing = Facing.BACK
				} else {
					camera.facing = Facing.FRONT
				}
				cameraViewModel.cameraChanged()
			}
		})

		setHasOptionsMenu(true)
		return binding.root
	}


	override fun onDestroyView() {
		camera.close()
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
