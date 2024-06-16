package com.yogadimas.tokoalattulissumberjaya.helper


import java.text.NumberFormat
import java.util.*


val locale = Locale("in", "ID")
fun String.formatRupiah(): String {
    val uang = this.toDouble()
    val mCurrencyFormat = NumberFormat.getCurrencyInstance(locale)
    return mCurrencyFormat.format(uang)
}