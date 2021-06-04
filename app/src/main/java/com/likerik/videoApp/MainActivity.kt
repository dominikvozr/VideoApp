package com.likerik.videoApp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.datatransport.runtime.backends.BackendResponse.ok
import com.google.android.material.snackbar.Snackbar
import com.uxcam.UXCam;


class MainActivity : AppCompatActivity() {

	private lateinit var navController: NavController

	private val requestPermissionLauncher =
			registerForActivityResult(
					ActivityResultContracts.RequestPermission()
			) { isGranted: Boolean ->
				if (isGranted) {
					// Permission has been granted. Start camera preview Activity.
					navController = this.findNavController(R.id.camera_nav_host_fragment)
					NavigationUI.setupActionBarWithNavController(this, navController)
				} else {

				}
			}


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		setSupportActionBar(findViewById(R.id.toolbar))
		UXCam.startWithKey("i8p8xcw3jc0wg79")

		val requestPermissionLauncher =
				registerForActivityResult(ActivityResultContracts.RequestPermission()
				) { isGranted: Boolean ->
					if (isGranted) {
						navController = this.findNavController(R.id.camera_nav_host_fragment)
						NavigationUI.setupActionBarWithNavController(this, navController)
						// Permission is granted. Continue the action or workflow in your
						// app.
					} else {
						// Explain to the user that the feature is unavailable because the
						// features requires a permission that the user has denied. At the
						// same time, respect the user's decision. Don't link to system
						// settings in an effort to convince the user to change their
						// decision.
					}
				}

		if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
				== PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
				== PackageManager.PERMISSION_GRANTED) {

		} else requestCameraPermission()

		when (PackageManager.PERMISSION_GRANTED) {
			ContextCompat.checkSelfPermission(
					applicationContext,
					Manifest.permission.MANAGE_EXTERNAL_STORAGE
			) -> {
				navController = this.findNavController(R.id.camera_nav_host_fragment)
				NavigationUI.setupActionBarWithNavController(this, navController)
			}
			else -> {
				// You can directly ask for the permission.
				// The registered ActivityResultCallback gets the result of this request.
				requestPermissionLauncher.launch(
						Manifest.permission.MANAGE_EXTERNAL_STORAGE)
			}
		}


		//supportActionBar?.hide()
    }

	/**
	 * Requests the [android.Manifest.permission.CAMERA] permission.
	 * If an additional rationale should be displayed, the user has to launch the request from
	 * a SnackBar that includes additional information.
	 */
	private fun requestCameraPermission() {
		if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
				shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
			// Provide an additional rationale to the user if the permission was not granted
			// and the user would benefit from additional context for the use of the permission.
			// Display a SnackBar with a button to request the missing permission.
			navController = this.findNavController(R.id.camera_nav_host_fragment)
			NavigationUI.setupActionBarWithNavController(this, navController)
		} else {
			// You can directly ask for the permission.
			requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
			requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
		}
	}

	override fun onSupportNavigateUp(): Boolean {
		return navController.navigateUp()
	}
}
