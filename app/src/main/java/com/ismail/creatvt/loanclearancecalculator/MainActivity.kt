package com.ismail.creatvt.loanclearancecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import com.ismail.creatvt.loanclearancecalculator.ui.theme.LoanClearanceCalculatorTheme

val LIGHT_SKY_BLUE = Color(0xFFD6F0FF)
val OFF_WHITE = Color(0xFFDFDFDF)
val BLACKISH_BLUE = Color(0xFF133C53)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var loanForm by rememberSaveable {
                mutableStateOf(true)
            }
            var loanData by rememberSaveable {
                mutableStateOf<LoanData?>(null)
            }

            LoanClearanceCalculatorTheme {
                enableEdgeToEdge(
                    statusBarStyle = MaterialTheme.colorScheme.primaryContainer.let {
                        SystemBarStyle.light(
                            it.toArgb(), it.toArgb()
                        )
                    }
                )
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (loanForm) {
                        LoanFormScreen(
                            modifier = Modifier.padding(innerPadding),
                            loanData
                        ) {
                            loanData = it
                            loanForm = false
                        }
                    } else {
                        LoanStatement(
                            modifier = Modifier.padding(innerPadding),
                            loanData!!
                        ) {
                            loanForm = true
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LoanClearanceCalculatorTheme {
        LoanFormScreen(initLoanData = null) {

        }
    }
}