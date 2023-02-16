package com.bmtnuinstitute.pointofsales.util

import android.app.Activity
import android.app.DatePickerDialog
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class Helper {
    companion object {
        fun idrFormat(number: Int): String{
            val decimalFormat: NumberFormat = DecimalFormat("#,###")
            return decimalFormat.format(number)
        }
    }
}