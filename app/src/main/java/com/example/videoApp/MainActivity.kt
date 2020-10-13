package com.example.videoApp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.videoApp.fragments.MainFragmentDirections
import com.example.videoApp.fragments.VideoPlayerFragmentDirections


class MainActivity : AppCompatActivity() {

	private lateinit var navController: NavController


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		setSupportActionBar(findViewById(R.id.toolbar))
		navController = this.findNavController(R.id.camera_nav_host_fragment)
		NavigationUI.setupActionBarWithNavController(this, navController)
    }

	override fun onSupportNavigateUp(): Boolean {
		return navController.navigateUp()
	}

	override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
		R.id.action_my_videos -> {
			navController.navigate(VideoPlayerFragmentDirections.actionVideoPlayerFragmentToMainFragment())
			true
		}

		R.id.action_server_videos -> {
			navController.navigate(MainFragmentDirections.actionMainFragmentToServerSampleFragment())
			true
		}

		else -> {
			super.onOptionsItemSelected(item)
		}
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		// Inflate the menu; this adds items to the action bar if it is present.
		menuInflater.inflate(R.menu.menu, menu)
		return true
	}
}
