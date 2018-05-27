package com.ryooku.mylittlebrick.application

interface AppConfig {
    companion object {
        const val PACKAGE_NAME = "com.ryooku.mylittlebirck"
        const val DB_NAME = "brickDatabase"
        const val DB_VERSION = 1
        const val DB_FILE_NAME = "BrickList.db"
        const val DB_PATH = "/databases/"

        const val DEFAULT_HOST = "http://fcds.cs.put.poznan.pl/MyWeb/BL"
        const val FILE_EXTENSION = ".xml"

        const val DEFAULT_IMAGE_SOURCE = "https://www.lego.com/service/bricks/"
        const val ALT_IMAGE_SOURCE = "http://img.bricklink.com/"
        const val THIRD_IMAGE_SOURCE = "http://bricklink.com/"
    }

}