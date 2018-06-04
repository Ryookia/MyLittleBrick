package com.ryooku.mylittlebrick.utils

class SynchronizedCounter {
    private var count = 0

    @Synchronized
    fun increaseCounter() {
        count++
    }

    @Synchronized
    fun decreaseCounter() {
        count--
    }

    @Synchronized
    fun setValue(value: Int) {
        count = value
    }

    @Synchronized
    fun resetCounter() {
        count = 0
    }

    @Synchronized
    fun getValue(): Int {
        return count
    }
}