package com.ismail.creatvt.loanclearancecalculator

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.Calendar
import kotlin.jvm.optionals.getOrNull

@Parcelize
data class PaymentSchedule(
    val payments: List<PaymentItem>
) : Parcelable {
    @IgnoredOnParcel
    var totalInterestPaid = payments.stream().map { it.interest }.reduce { t, u -> t + u }.getOrNull()?:0.0
    @IgnoredOnParcel
    var totalAmountPaid = payments.stream().map { it.emi + it.prepayment }.reduce { t, u -> t + u }.getOrNull()?:0.0


    companion object {
        fun generate(loanData: LoanData, specialMonths: List<LoanMonth>) = PaymentSchedule(
            buildList {
                var loanAmount = loanData.loanAmount
                val calendar = Calendar.getInstance()
                if (calendar.get(Calendar.DAY_OF_MONTH) > 7) {
                    calendar.add(Calendar.MONTH, 1)
                }
                while (loanAmount >= 0.01) {
                    val interest = loanAmount * loanData.interest / 1200
                    val maxPrepayment = loanAmount - (loanData.monthlyEMI - interest)

                    val loanMonth = LoanMonth(
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.YEAR),
                        loanData.prepayment.coerceAtMost(maxPrepayment),
                        maxPrepayment
                    )

                    val specialMonth = specialMonths.find { it.sameDate(loanMonth) }
                    if (specialMonth != null) {
                        loanMonth.prepayment = specialMonth.prepayment.coerceAtMost(loanMonth.maxPrepayment)
                    }
                    val (emi, prepayment) = if (loanData.monthlyEMI >= loanAmount + interest) {
                        loanAmount + interest to 0.0
                    } else {
                        loanData.monthlyEMI to loanMonth.prepayment
                    }
                    val paymentItem = PaymentItem(
                        loanMonth,
                        emi,
                        prepayment,
                        interest,
                        loanMonth.prepayment == 0.0
                    )
                    loanAmount -= paymentItem.totalPaid
                    calendar.add(Calendar.MONTH, 1)
                    add(paymentItem)
                }
            }
        )
    }
}