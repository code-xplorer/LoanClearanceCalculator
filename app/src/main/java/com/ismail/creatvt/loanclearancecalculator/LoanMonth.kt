package com.ismail.creatvt.loanclearancecalculator

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Calendar

@Parcelize
data class LoanMonth(val month: Int, val year:Int, var prepayment: Double, var maxPrepayment: Double):Parcelable {

    private val monthString: String
        get() = when(month) {
            Calendar.JANUARY -> "Jan"
            Calendar.FEBRUARY -> "Feb"
            Calendar.MARCH -> "Mar"
            Calendar.APRIL -> "Apr"
            Calendar.MAY -> "May"
            Calendar.JUNE -> "Jun"
            Calendar.JULY -> "Jul"
            Calendar.AUGUST -> "Aug"
            Calendar.SEPTEMBER -> "Sep"
            Calendar.OCTOBER -> "Oct"
            Calendar.NOVEMBER -> "Nov"
            Calendar.DECEMBER -> "Dec"
            else -> "---"
        }

    override fun equals(other: Any?): Boolean {
        if(other is LoanMonth) {
            return other.month == month && other.year == year && other.prepayment == prepayment && other.maxPrepayment == maxPrepayment
        }
        return false
    }

    fun sameDate(other: LoanMonth):Boolean {
        return other.month == month && other.year == year
    }

    override fun hashCode(): Int {
        var result = month
        result = 31 * result + year
        return result
    }

    override fun toString(): String {
        return "$monthString $year"
    }
}
