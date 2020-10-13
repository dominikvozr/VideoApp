package com.example.videoApp.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class RecyclerViewScrollListener : RecyclerView.OnScrollListener() {
	private var firstVisibleItem = 0
	private var visibleItemCount = 0

	@Volatile
	private var mEnabled = true
	private var mPreLoadCount = 0

	override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
		super.onScrolled(recyclerView, dx, dy)
		if (mEnabled) {
			val layoutManager = recyclerView.layoutManager
			require(layoutManager is LinearLayoutManager) { "Expected recyclerview to have linear layout manager" }
			visibleItemCount = layoutManager.childCount
			firstVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition()
			onItemIsFirstVisibleItem(firstVisibleItem)
		}
	}



	abstract fun onItemIsFirstVisibleItem(index: Int)
	fun disableScrollListener() {
		mEnabled = false
	}

	fun enableScrollListener() {
		mEnabled = true
	}

	fun setPreLoadCount(mPreLoadCount: Int) {
		this.mPreLoadCount = mPreLoadCount
	}
}
