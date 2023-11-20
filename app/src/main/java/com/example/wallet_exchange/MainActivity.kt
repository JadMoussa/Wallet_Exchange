package com.example.wallet_exchange

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.wallet_exchange.ui.theme.Wallet_ExchangeTheme
import kotlinx.android.synthetic.main.activity_main

class MainActivity : ComponentActivity() {
    private val currencies = arrayOf("USD", "EUR", "GBP", "JPY")
    private lateinit var fromCurrency: String
    private lateinit var toCurrency: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setContent {
            Wallet_ExchangeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerFrom.adapter = adapter
                spinnerTo.adapter = adapter

                // Convert button click listener
                btnConvert.setOnClickListener {
                    fromCurrency = spinnerFrom.selectedItem.toString()
                    toCurrency = spinnerTo.selectedItem.toString()
                    val amount = etAmount.text.toString().toDoubleOrNull()

                    if (amount != null) {
                        val convertedAmount = convertCurrency(amount)
                        tvResult.text = String.format("%.2f %s", convertedAmount, toCurrency)
                    } else {
                        Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            private fun convertCurrency(amount: Double): Double {
                // Replace this with actual currency conversion logic (API, rates, etc.)
                // For demonstration, a simple hardcoded conversion is used
                return when (fromCurrency) {
                    "USD" -> when (toCurrency) {
                        "EUR" -> amount * 0.82
                        "GBP" -> amount * 0.74
                        "JPY" -> amount * 110.0
                        else -> amount
                    }
                    "EUR" -> when (toCurrency) {
                        "USD" -> amount * 1.22
                        "GBP" -> amount * 0.91
                        "JPY" -> amount * 128.12
                        else -> amount
                    }
                    "GBP" -> when (toCurrency) {
                        "USD" -> amount * 1.35
                        "EUR" -> amount * 1.10
                        "JPY" -> amount * 141.79
                        else -> amount
                    }
                    "JPY" -> when (toCurrency) {
                        "USD" -> amount * 0.0091
                        "EUR" -> amount * 0.0078
                        "GBP" -> amount * 0.0070
                        else -> amount
                    }
                    else -> amount
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Wallet_ExchangeTheme {
        Greeting("Android")
    }
}