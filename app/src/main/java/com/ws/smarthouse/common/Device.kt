package com.ws.smarthouse.common

import android.text.format.DateFormat
import java.util.*

class Device internal constructor(x: Float, y: Float, typeIndex: Int) {

    var x = 0f
    var y = 0f
    var isSelected = 0
    var typeIndex = 0
    var isOn = true
    var temperature = 26
    var date = Date(Date().time + 2 * 60 * 60 * 1000)
    var selectedColor = 0
    val nextCleaningDate: String
        get() = DateFormat.format("dd MMMM", date).toString()
    val nextCleaningTime: String
        get() = DateFormat.format("HH:mm,", date).toString()

    init {
        this.x = x
        this.y = y
        this.typeIndex = typeIndex
    }
}