package com.ismail.creatvt.loanclearancecalculator

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PaymentDetail(
    paymentSchedule: PaymentSchedule,
    specialMonths: List<LoanMonth>,
    updateSpecialMonths: (List<LoanMonth>) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(10.dp),
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        itemsIndexed(paymentSchedule.payments, key = { _, item -> item }) { index, paymentItem ->
            OutlinedCard(
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
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
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${index + 1}", fontSize = 14.sp, modifier = Modifier
                                .width(40.dp)
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(4.dp),
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
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
                    Spacer(
                        modifier = Modifier
                            .height(0.5.dp)
                            .fillMaxWidth()
                            .background(Color.LightGray)
                    )

                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        PaymentComponent(
                            title = "EMI",
                            value = paymentItem.emi.currencyFormat
                        )
                        PaymentComponent(
                            title = "Interest",
                            value = paymentItem.interest.currencyFormat
                        )
                        PaymentComponent(
                            title = "Prepayment",
                            value = paymentItem.prepayment.currencyFormat,
                            shouldShowArrowButtons = true,
                            arrowUpEnabled = paymentItem.month.prepayment < paymentItem.month.maxPrepayment,
                            arrowDownEnabled = paymentItem.month.prepayment > 0,
                            onArrowUpClicked = {
                                updateSpecialMonths(buildList {
                                    addAll(specialMonths.filter {
                                        !it.sameDate(
                                            paymentItem.month
                                        )
                                    })
                                    paymentItem.month.prepayment += 10000.0
                                    add(paymentItem.month)
                                })
                            },
                            onArrowDownClicked = {
                                updateSpecialMonths(buildList {
                                    addAll(specialMonths.filter {
                                        !it.sameDate(
                                            paymentItem.month
                                        )
                                    })
                                    val intPrepayment = paymentItem.month.prepayment.toInt()
                                    if (intPrepayment % 10000 == 0) {
                                        paymentItem.month.prepayment -= 10000.0
                                    } else {
                                        paymentItem.month.prepayment =
                                            (intPrepayment - intPrepayment % 10000).toDouble()
                                    }
                                    add(paymentItem.month)
                                })
                            }
                        )
                        PaymentComponent(
                            title = "Total Paid",
                            value = paymentItem.totalPaid.currencyFormat
                        )
                    }
                }

            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRowScope.PaymentComponent(
    title: String,
    value: String,
    shouldShowArrowButtons: Boolean = false,
    onArrowUpClicked: () -> Unit = {},
    onArrowDownClicked: () -> Unit = {},
    arrowUpEnabled: Boolean = false,
    arrowDownEnabled: Boolean = false
) {
    Row(
        modifier = Modifier
            .weight(1f, fill = true)
            .widthIn(min = 100.dp, max = 120.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
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
        if (shouldShowArrowButtons) {
            Column {
                Image(
                    modifier = if (arrowUpEnabled) Modifier.clickable {
                        onArrowUpClicked()
                    } else Modifier,
                    imageVector = Icons.Filled.KeyboardArrowUp,
                    colorFilter = ColorFilter.tint(
                        if (arrowUpEnabled) Color.Black else Color.LightGray
                    ),
                    contentDescription = "Up"
                )
                Image(
                    modifier = if (arrowDownEnabled) Modifier.clickable {
                        onArrowDownClicked()
                    } else Modifier,
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    colorFilter = ColorFilter.tint(
                        if (arrowDownEnabled) Color.Black else Color.LightGray
                    ),
                    contentDescription = "Down"
                )
            }
        }
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
