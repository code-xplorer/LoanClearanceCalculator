package com.ismail.creatvt.loanclearancecalculator

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun LoanStatement(modifier: Modifier = Modifier, loanData: LoanData, onEdit: () -> Unit) {
    var specialMonths by rememberSaveable {
        mutableStateOf(listOf<LoanMonth>())
    }
    var paymentSchedule by rememberSaveable { mutableStateOf(PaymentSchedule(listOf())) }

    LaunchedEffect(loanData, specialMonths) {
        paymentSchedule = PaymentSchedule.generate(loanData, specialMonths)
    }

    BackHandler {
        onEdit()
    }

    Column(
        modifier
            .fillMaxSize()
    ) {
        PaymentSummary(paymentSchedule)
        PaymentDetail(paymentSchedule, specialMonths) {
            specialMonths = it
        }
    }

}

@Composable
fun PaymentDetail(
    paymentSchedule: PaymentSchedule,
    specialMonths: List<LoanMonth>,
    updateSpecialMonths: (List<LoanMonth>) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {

        itemsIndexed(paymentSchedule.payments, key = { _, item -> item }) { index, paymentItem ->
            OutlinedCard(border = BorderStroke(1.dp, Color.LightGray)) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                updateSpecialMonths(buildList {
                                    addAll(specialMonths.filter { !it.sameDate(paymentItem.month) })
                                    if (!paymentItem.isSkipped) {
                                        paymentItem.month.prepayment = 0.0
                                        add(paymentItem.month)
                                    }
                                })
                            }
                            .padding(10.dp, 12.dp, 10.dp, 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "${index + 1}", fontSize = 14.sp, modifier = Modifier
                            .width(35.dp)
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.shapes.small
                            )
                            .padding(4.dp),
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center)
                        Text(
                            fontSize = 14.sp,
                            text = paymentItem.month.toString()
                        )

                        Image(
                            painter = painterResource(id = R.drawable.baseline_block_24),
                            colorFilter = ColorFilter.tint(
                                if (paymentItem.isSkipped) Color.Red else OFF_WHITE
                            ),
                            contentDescription = "Skipped"
                        )
                    }
                    Spacer(modifier = Modifier.height(0.5.dp).fillMaxWidth().background(Color.LightGray))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Max),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(16.dp, 8.dp, 4.dp, 8.dp)
                        ) {
                            PaymentComponent(title = "EMI", value = paymentItem.emi.currencyFormat)
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PaymentComponent(
                                    title = "Prepayment",
                                    value = paymentItem.prepayment.currencyFormat
                                )

                                Column {
                                    Image(
                                        modifier = if (paymentItem.month.prepayment < paymentItem.month.maxPrepayment) Modifier.clickable {
                                            updateSpecialMonths(buildList {
                                                addAll(specialMonths.filter {
                                                    !it.sameDate(
                                                        paymentItem.month
                                                    )
                                                })
                                                paymentItem.month.prepayment += 10000.0
                                                add(paymentItem.month)
                                            })
                                        } else Modifier,
                                        imageVector = Icons.Filled.KeyboardArrowUp,
                                        colorFilter = if (paymentItem.month.prepayment < paymentItem.month.maxPrepayment) ColorFilter.tint(
                                            Color.Black
                                        ) else ColorFilter.tint(Color.LightGray),
                                        contentDescription = "Up"
                                    )
                                    Image(
                                        modifier = if (paymentItem.month.prepayment > 0) Modifier.clickable {
                                            updateSpecialMonths(buildList {
                                                addAll(specialMonths.filter {
                                                    !it.sameDate(
                                                        paymentItem.month
                                                    )
                                                })
                                                val intPrepayment = paymentItem.month.prepayment.toInt()
                                                if(intPrepayment % 10000 == 0) {
                                                    paymentItem.month.prepayment -= 10000.0
                                                } else {
                                                    paymentItem.month.prepayment = (intPrepayment - intPrepayment % 10000).toDouble()
                                                }
                                                add(paymentItem.month)
                                            })
                                        } else Modifier,
                                        imageVector = Icons.Filled.KeyboardArrowDown,
                                        colorFilter = if (paymentItem.month.prepayment > 0) ColorFilter.tint(
                                            Color.Black
                                        ) else ColorFilter.tint(Color.LightGray),
                                        contentDescription = "Down"
                                    )
                                }
                            }
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(4.dp, 12.dp, 8.dp, 12.dp)
                        ) {
                            PaymentComponent(
                                title = "Interest",
                                value = paymentItem.interest.currencyFormat
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            PaymentComponent(
                                title = "Total Paid",
                                value = paymentItem.totalPaid.currencyFormat
                            )
                        }
                    }
                }


            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun PaymentComponent(title: String, value: String) {
    Column {
        Text(
            text = title,
            fontSize = 12.sp,
            color = Color.Gray
        )

        Text(
            text = value,
            fontSize = 14.sp
        )
    }
}

@Composable
fun PaymentSummary(paymentSchedule: PaymentSchedule) {
    Row(
        Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(20.dp, 14.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Total Amount Paid", fontSize = 12.sp, color = BLACKISH_BLUE)
            Text(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                text = paymentSchedule.totalAmountPaid.currencyFormat
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Total Interest Paid", fontSize = 12.sp, color = BLACKISH_BLUE)
            Text(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                text = paymentSchedule.totalInterestPaid.currencyFormat
            )
        }
    }

    Spacer(
        modifier = Modifier
            .height(1.dp)
            .fillMaxWidth()
            .background(Color.Black)
    )
}
