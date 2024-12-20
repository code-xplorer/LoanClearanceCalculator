package com.ismail.creatvt.loanclearancecalculator

import java.text.NumberFormat
import java.util.Locale

fun String.processNumber(): String {
    return (if (contains(".")) {
        split(".").subList(0, 2).joinToString(".")
    } else "$this.0").let { v0 ->
        if (v0[v0.length - 1] == '0' && v0[v0.length - 2] != '.') {
            v0.substring(0, v0.length - 1)
        } else if (v0[0] == '.') {
            "0$v0"
        } else if (v0[v0.length - 1] == '.') {
            "${v0}0"
        } else v0
    }.replace("-", "").replace(",", "").replace(" ", "")
}

val Double.currencyFormat: String
    get() = NumberFormat.getCurrencyInstance(Locale("en", "IN")).format(this)