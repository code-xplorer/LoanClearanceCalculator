package com.ismail.creatvt.loanclearancecalculator

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanFormScreen(
    modifier: Modifier = Modifier,
    initLoanData: LoanData?,
    onDone: (LoanData) -> Unit
) {
    var loanAmount by rememberSaveable {
        mutableStateOf(initLoanData?.loanAmount?.let { String.format(Locale.ENGLISH, "%.2f", it) }
            ?: "2298753.0")
    }
    var interestRate by rememberSaveable {
        mutableStateOf(initLoanData?.interest?.let { String.format(Locale.ENGLISH, "%.2f", it) }
            ?: "8.7")
    }
    var monthlyEMI by rememberSaveable {
        mutableStateOf(initLoanData?.monthlyEMI?.let { String.format(Locale.ENGLISH, "%.2f", it) }
            ?: "26509.0")
    }
    var prepayment by rememberSaveable {
        mutableStateOf(initLoanData?.prepayment?.let { String.format(Locale.ENGLISH, "%.2f", it) }
            ?: "60000.0")
    }
    val scrollState = rememberScrollState()

    val submitForm:()->Unit = {
        onDone(
            LoanData(
                loanAmount.toDoubleOrNull() ?: 0.0,
                monthlyEMI.toDoubleOrNull() ?: 0.0,
                prepayment.toDoubleOrNull() ?: 0.0,
                interestRate.toDoubleOrNull() ?: 0.0
            )
        )
    }
    Box {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primaryContainer).padding(20.dp),
                text = "Loan Repayment Calculator",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
                    .background(Color.Black)
            )
            Column(modifier = Modifier
                .padding(20.dp, 32.dp, 20.dp, 100.dp)) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        unfocusedLeadingIconColor = Color.Gray,
                        focusedLeadingIconColor = Color.Black
                    ),
                    leadingIcon = { Text("₹", fontSize = 18.sp)},
                    value = loanAmount,
                    label = { Text(text = "Loan Amount") },
                    onValueChange = {
                        loanAmount = it.processNumber()
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        unfocusedTrailingIconColor = Color.Gray,
                        focusedTrailingIconColor = Color.Black
                    ),
                    trailingIcon = { Text("%", fontSize = 18.sp)},
                    value = interestRate,
                    label = { Text(text = "Interest Rate (in %)") },
                    onValueChange = {
                        interestRate = it.processNumber()
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                )
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        unfocusedLeadingIconColor = Color.Gray,
                        focusedLeadingIconColor = Color.Black
                    ),
                    leadingIcon = { Text("₹", fontSize = 18.sp)},
                    value = monthlyEMI,
                    label = { Text(text = "Monthly EMI") },
                    onValueChange = {
                        monthlyEMI = it.processNumber()
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                )
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        unfocusedLeadingIconColor = Color.Gray,
                        focusedLeadingIconColor = Color.Black
                    ),
                    leadingIcon = { Text("₹", fontSize = 18.sp)},
                    value = prepayment,
                    label = { Text(text = "Monthly Pre-Payment Amount") },
                    onValueChange = {
                        prepayment = it.processNumber()
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions {
                        submitForm()
                    }
                )
            }

        }

        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .safeDrawingPadding()
                .padding(20.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            onClick = {
                submitForm()
            }) {
            Row(modifier = Modifier.padding(16.dp, 8.dp)) {
                Image(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Done",
                    colorFilter = ColorFilter.tint(Color.White)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "Done")
            }
        }
    }
}