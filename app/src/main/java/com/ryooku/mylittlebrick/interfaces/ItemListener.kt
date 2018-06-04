package com.ryooku.mylittlebrick.interfaces


interface ItemListener {
    fun itemSelected(position: Int)
    fun increase(position: Int)
    fun decrease(position: Int)
}