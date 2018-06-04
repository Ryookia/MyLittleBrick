package com.ryooku.mylittlebrick.dialogs

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import com.ryooku.mylittlebrick.R
import com.ryooku.mylittlebrick.activities.MainActivity
import kotlinx.android.synthetic.main.dialog_settings.*

class SettingsDialog(private val activity: Activity, private val listener: SettingsListener) : Dialog(activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = View.inflate(activity, R.layout.dialog_settings, null)
        setContentView(view)
        setDefaultUrl()
        initButtons()
    }

    private fun setDefaultUrl() {
        urlInput.setText((activity as MainActivity).preferenceHelper.getDefaultUrl())
    }

    private fun initButtons() {
        cancelButton.setOnClickListener { dismiss() }
        acceptButton.setOnClickListener { listener.onUrlChanged(urlInput.text.toString()); dismiss() }
    }

}