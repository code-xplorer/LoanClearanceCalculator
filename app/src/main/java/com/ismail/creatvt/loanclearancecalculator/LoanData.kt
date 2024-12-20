package com.ismail.creatvt.loanclearancecalculator

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoanData(
    val loanAmount: Double,
    val monthlyEMI: Double,
    val prepayment: Double,
    val interest: Double
):Parcelable
