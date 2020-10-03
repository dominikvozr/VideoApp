package com.example.videoApp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		//setSupportActionBar(findViewById(R.id.toolbar))
		val navController = this.findNavController(R.id.camera_nav_host_fragment)
		NavigationUI.setupActionBarWithNavController(this, navController)
    }

	override fun onSupportNavigateUp(): Boolean {
		val navController = this.findNavController(R.id.camera_nav_host_fragment)
		return navController.navigateUp()
	}
}
