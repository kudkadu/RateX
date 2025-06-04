package com.example.ratex.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ratex.model.Currency
import com.example.ratex.repository.ExchangeRateRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _fromCurrency = MutableLiveData(Currency.USD)
    private val _toCurrency = MutableLiveData(Currency.EUR)

    private val _conversionLabel = MutableLiveData<String>()
    val conversionLabel: LiveData<String> = _conversionLabel

    fun setFromCurrency(currency: Currency, labelProvider: (Currency) -> String) {
        _fromCurrency.value = currency
        updateLabel(labelProvider)
    }

    fun setToCurrency(currency: Currency, labelProvider: (Currency) -> String) {
        _toCurrency.value = currency
        updateLabel(labelProvider)
    }

    private fun updateLabel(labelProvider: (Currency) -> String) {
        val from = _fromCurrency.value
        val to = _toCurrency.value
        if (from != null && to != null) {
            val label = "${labelProvider(from)} (${from.code}) → ${labelProvider(to)} (${to.code})"
            _conversionLabel.value = label
        }
    }

    fun getCurrencyDisplayList(): List<String> {
        val context = getApplication<Application>().applicationContext
        return Currency.values().map { "${context.getString(it.labelId)} (${it.code})" }
    }

    fun getCurrencyList(): List<Currency> = Currency.values().toList()

    private val _conversionRate = MutableLiveData<Double?>()
    val conversionRate: LiveData<Double?> = _conversionRate
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun loadRate(from: String, to: String) {
        coroutineScope.launch {
            val rate = ExchangeRateRepository.fetchExchangeRate(from, to)
            _conversionRate.postValue(rate)
        }
    }

    override fun onCleared() {
        super.onCleared()
        coroutineScope.cancel()
    }

    fun setMockRate(rate: Double) {
        _conversionRate.postValue(rate)
    }
}
