package com.ryooku.mylittlebrick.dialogs

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window.FEATURE_NO_TITLE
import android.widget.Toast
import com.ryooku.mylittlebrick.R
import com.ryooku.mylittlebrick.activities.MainActivity
import com.ryooku.mylittlebrick.application.AppConfig
import kotlinx.android.synthetic.main.dialog_add_project.*


class AddProjectDialog(private val activity: Activity, private val listener: AddProjectListener) : Dialog(activity) {

    companion object {
        const val VALIDATE_OKAY = 0
        const val VALIDATE_INVALID_URL = 1
        const val VALIDATE_INVALID_ID = 2
        const val VALIDATE_INVALID_NAME = 3

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(FEATURE_NO_TITLE)
        val view = View.inflate(activity, R.layout.dialog_add_project, null)
        setContentView(view)
        setDefaultUrl()
        initButtons()
    }

    private fun setDefaultUrl() {
        urlInput.setText(AppConfig.DEFAULT_HOST)
        idInput.setText("615")
        nameInput.setText("Test")
    }

    private fun initButtons() {
        cancelButton.setOnClickListener { dismiss() }
        acceptButton.setOnClickListener({
            val validateResult = validate()
            if (validateResult == VALIDATE_OKAY) {
                listener.onAccept(
                        urlInput.text.toString(),
                        idInput.text.toString(),
                        nameInput.text.toString())
                dismiss()
            } else
                showErrorMessage(validateResult)
        })
    }

    private fun showErrorMessage(result: Int) {
        val message = when (result) {
            VALIDATE_INVALID_URL -> R.string.invalid_url
            VALIDATE_INVALID_NAME -> R.string.invalid_name
            VALIDATE_INVALID_ID -> R.string.invalid_id
            else -> R.string.unknown_error
        }
        (activity as MainActivity).showMessageToast(message, Toast.LENGTH_LONG)
    }

    private fun validate(): Int {
        if (urlInput.text.isEmpty()) return VALIDATE_INVALID_URL
        if (idInput.text.isEmpty()) return VALIDATE_INVALID_ID
        if (nameInput.text.isEmpty()) return VALIDATE_INVALID_NAME
        return VALIDATE_OKAY
    }



}