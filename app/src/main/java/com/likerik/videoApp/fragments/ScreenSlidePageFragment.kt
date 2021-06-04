package com.likerik.videoApp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.likerik.videoApp.R
import com.likerik.videoApp.databinding.FragmentScreenSlidePageBinding

class ScreenSlidePageFragment : Fragment() {

	private lateinit var binding: FragmentScreenSlidePageBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_screen_slide_page, container, false)

		/*val pagerAdapter = ScreenSlidePagerAdapter(this)
		viewPager.adapter = pagerAdapter*/

		return binding.root
	}
}
