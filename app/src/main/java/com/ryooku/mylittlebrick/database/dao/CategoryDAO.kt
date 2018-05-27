package com.ryooku.mylittlebrick.database.dao

public class CategoryDAO(public var id: Long) {

    constructor(id: Long, code: Long, name: String, namePL: String) : this(id) {
        this.code = code
        this.name = name
        this.namePL = namePL
    }

    public var code: Long? = null
    public lateinit var name: String
    public lateinit var namePL: String


}