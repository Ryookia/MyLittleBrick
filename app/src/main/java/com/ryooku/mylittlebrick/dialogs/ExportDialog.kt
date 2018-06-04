package com.ryooku.mylittlebrick.dialogs

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import com.ryooku.mylittlebrick.R
import kotlinx.android.synthetic.main.dialog_export.*

class ExportDialog(private val activity: Activity, private val listener: ExportListener) : Dialog(activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = View.inflate(activity, R.layout.dialog_export, null)
        setContentView(view)
        setDefaultUrl()
        initButtons()
    }

    private fun setDefaultUrl() {
        nameInput.setText("pliczek")
    }

    private fun initButtons() {
        cancelButton.setOnClickListener { dismiss() }
        acceptButton.setOnClickListener {
            if (!nameInput.text.toString().isEmpty()) {
                listener.onNameSelected(nameInput.text.toString())
                dismiss()
            }
        }
    }
}