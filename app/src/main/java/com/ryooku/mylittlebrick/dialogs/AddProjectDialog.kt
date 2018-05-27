package com.ryooku.mylittlebrick.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window.FEATURE_NO_TITLE
import com.ryooku.mylittlebrick.R


class AddProjectDialog(context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(FEATURE_NO_TITLE)
        val view = View.inflate(context, R.layout.dialog_add_project, null)
        setContentView(view)
    }


}