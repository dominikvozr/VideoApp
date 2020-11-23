package com.example.videoApp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI


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


}
