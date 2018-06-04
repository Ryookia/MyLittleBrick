package com.ryooku.mylittlebrick.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.StrictMode
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.ryooku.mylittlebrick.R
import com.ryooku.mylittlebrick.application.App
import com.ryooku.mylittlebrick.database.Database
import com.ryooku.mylittlebrick.utils.FragmentHelper
import com.ryooku.mylittlebrick.utils.PreferenceHelper
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var infoToast: Toast
    private var progressVisible: Boolean = false
    private var progressInAnimation: Boolean = false
    lateinit var fragmentHelper: FragmentHelper
    lateinit var preferenceHelper: PreferenceHelper
    var database: Database? = null

    @SuppressLint("ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        infoToast = Toast.makeText(this, "", Toast.LENGTH_SHORT)
        fragmentHelper = FragmentHelper(supportFragmentManager, R.id.mainFrame)
        preferenceHelper = (application as App).preferenceHelper!!
        database = (application as App).database
        fragmentHelper.setProjectListFragment()

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }

    fun showProgressLayout() {
        showProgressLayout("")
    }

    fun showProgressLayout(stringId: Int) {
        showProgressLayout(getString(stringId))
    }

    fun showProgressLayout(message: String) {
        progressVisible = true
        progressLayout.visibility = View.VISIBLE
        progressText.text = message
    }

    fun hideProgressLayout() {
        if (progressInAnimation || !progressVisible) return
        progressInAnimation = true
        val animation = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                animation.setAnimationListener(null)
                progressLayout.visibility = View.GONE
                progressInAnimation = false
                progressVisible = false
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        progressLayout.startAnimation(animation)
    }

    fun showMessageToast(stringId: Int, duration: Int) {
        showMessageToast(getString(stringId), duration)
    }

    fun showMessageToast(message: String, duration: Int) {
        infoToast.setText(message)
        infoToast.duration = duration
        infoToast.show()
    }
}
