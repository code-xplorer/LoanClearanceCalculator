package com.ismail.creatvt.loanclearancecalculator

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentItem(
    val month: LoanMonth,
    val emi: Double,
    var prepayment: Double,
    val interest: Double,
    val isSkipped: Boolean
):Parcelable {
    val totalPaid: Double
        get() = emi + prepayment - interest
}
