package com.ryooku.mylittlebrick.utils

import android.support.v4.app.FragmentManager
import com.ryooku.mylittlebrick.R
import com.ryooku.mylittlebrick.fragments.ProjectFragment
import com.ryooku.mylittlebrick.fragments.ProjectListFragment

class FragmentHelper(private val manager: FragmentManager, private val defaultFrame: Int) {

    fun setProjectListFragment() {
        manager.beginTransaction().replace(defaultFrame, ProjectListFragment.newInstance()).commit()
    }

    fun setProjectFragment(projectId: Int) {
        manager.beginTransaction()
                .addToBackStack("")
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_right_out, R.anim.slide_in_left, R.anim.slide_out_left)
                .replace(defaultFrame, ProjectFragment.newInstance(projectId))
                .commit()
    }

    fun getBackStackEntryCount(): Int {
        return manager.backStackEntryCount ?: -1
    }

    fun popBackStack() {
        if (getBackStackEntryCount() > 0)
            manager.popBackStack()
    }
}