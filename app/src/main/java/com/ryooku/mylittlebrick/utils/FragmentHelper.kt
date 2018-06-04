package com.ryooku.mylittlebrick.utils

import android.support.v4.app.FragmentManager
import com.ryooku.mylittlebrick.fragments.ProjectFragment
import com.ryooku.mylittlebrick.fragments.ProjectListFragment

class FragmentHelper(private val manager: FragmentManager, private val defaultFrame: Int) {

    fun setProjectListFragment() {
        manager.beginTransaction().replace(defaultFrame, ProjectListFragment.newInstance()).commit()
    }

    fun setProjectFragment(projectId: Int) {
        manager.beginTransaction().addToBackStack("").replace(defaultFrame, ProjectFragment.newInstance(projectId)).commit()
    }

}