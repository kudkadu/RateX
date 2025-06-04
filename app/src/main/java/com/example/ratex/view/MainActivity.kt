package com.example.ratex.view

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.ratex.R
import com.example.ratex.model.Currency
import com.example.ratex.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    private lateinit var spinnerFrom: Spinner
    private lateinit var spinnerTo: Spinner
    private lateinit var conversionLabel: TextView
    private lateinit var rateLabel: TextView  // Новое поле для котировки

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinnerFrom = findViewById(R.id.spinnerFrom)
        spinnerTo = findViewById(R.id.spinnerTo)
        conversionLabel = findViewById(R.id.conversionLabel)
        rateLabel = findViewById(R.id.rateLabel)

        val currencies = Currency.entries.toTypedArray()
        val currencyList = viewModel.getCurrencyDisplayList()

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencyList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerFrom.adapter = adapter
        spinnerTo.adapter = adapter

        // Подписка на LiveData из ViewModel
        viewModel.conversionLabel.observe(this) { label ->
            //Log.d("RateX", "Label updated to $label")
            conversionLabel.text = label
        }

        // Подписка на численное значение курса
        viewModel.conversionRate.observe(this) { rate ->
            rateLabel.text = if (rate != null)
                getString(R.string.rate_prefix) + " %.2f".format(rate)
            else
                getString(R.string.rate_error)
        }

        val listener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val from = currencies[spinnerFrom.selectedItemPosition]
                val to = currencies[spinnerTo.selectedItemPosition]
                viewModel.setFromCurrency(from) { getString(it.labelId) }
                viewModel.setToCurrency(to) { getString(it.labelId) }

                // Запросить курс валют
                if (from != to) {
                    viewModel.loadRate(from.code, to.code)
                } else {
                    // Если одинаковые, сразу отобразим 1.0
                    viewModel.setMockRate(1.0)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerFrom.onItemSelectedListener = listener
        spinnerTo.onItemSelectedListener = listener
    }
}
