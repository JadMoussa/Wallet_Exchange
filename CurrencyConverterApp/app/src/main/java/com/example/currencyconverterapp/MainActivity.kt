import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var fromCurrencySpinner: Spinner
    private lateinit var toCurrencySpinner: Spinner
    private lateinit var amountEditText: EditText
    private lateinit var resultTextView: TextView

    // API endpoint for fetching exchange rates
    private val apiEndpoint = "https://655b40e1ab37729791a8c71e.mockapi.io/converter/api/v1/exchange_values"

    // Exchange rates fetched from the API
    private var exchangeRates: Map<String, Double> = emptyMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fromCurrencySpinner = findViewById(R.id.fromCurrencySpinner)
        toCurrencySpinner = findViewById(R.id.toCurrencySpinner)
        amountEditText = findViewById(R.id.amountEditText)
        resultTextView = findViewById(R.id.resultTextView)

        // Fetch exchange rates from the API in the background
        FetchExchangeRatesTask().execute(apiEndpoint)

        // Set default selection for the spinners
        fromCurrencySpinner.setSelection(0)
        toCurrencySpinner.setSelection(1)
    }

    fun convertCurrency(view: View) {
        val fromCurrency = fromCurrencySpinner.selectedItem.toString()
        val toCurrency = toCurrencySpinner.selectedItem.toString()
        val amount = amountEditText.text.toString().toDoubleOrNull()

        if (amount != null) {
            val convertedAmount = convertAmount(amount, fromCurrency, toCurrency)
            resultTextView.text = String.format("%.2f %s", convertedAmount, toCurrency)
        } else {
            resultTextView.text = "Invalid amount"
        }
    }

    private fun convertAmount(amount: Double, fromCurrency: String, toCurrency: String): Double {
        val fromRate = exchangeRates[fromCurrency] ?: 1.0
        val toRate = exchangeRates[toCurrency] ?: 1.0
        return amount * (toRate / fromRate)
    }

    // AsyncTask to fetch exchange rates from the API in the background
    private inner class FetchExchangeRatesTask : AsyncTask<String, Void, Map<String, Double>>() {

        override fun doInBackground(vararg params: String?): Map<String, Double> {
            val apiUrl = params[0]
            return fetchExchangeRates(apiUrl)
        }

        override fun onPostExecute(result: Map<String, Double>?) {
            super.onPostExecute(result)
            if (result != null) {
                exchangeRates = result
            } else {
                // Handle error or inform the user about the issue
            }
        }

        private fun fetchExchangeRates(apiUrl: String?): Map<String, Double> {
            val rates = mutableMapOf<String, Double>()

            try {
                val url = URL(apiUrl)
                val urlConnection = url.openConnection() as HttpURLConnection

                try {
                    val inputStream = urlConnection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val response = StringBuilder()
                    var line: String?

                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }

                    val jsonArray = JSONArray(response.toString())

                    // Extract exchange rates from the JSON response
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                        val currencyName = jsonObject.getString("currency_name")
                        val exchangeValue = jsonObject.getInt("value").toDouble()
                        rates[currencyName] = exchangeValue
                    }

                } finally {
                    urlConnection.disconnect()
                }
            } catch (e: Exception) {
                // Handle exception (e.g., network error)
            }

            return rates
        }
    }
}
