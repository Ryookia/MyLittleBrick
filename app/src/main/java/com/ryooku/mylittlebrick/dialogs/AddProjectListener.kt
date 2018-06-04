package com.ryooku.mylittlebrick.dialogs

interface AddProjectListener {
    fun onAccept(url: String, id: String, name: String)
}