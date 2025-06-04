package com.example.ratex.repository

import android.util.Log
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object ExchangeRateRepository {
    fun fetchExchangeRate(from: String, to: String): Double? {
        try {
            if (from == to) return 1.0 // Оптимизация: одинаковые валюты

            val urlStr = "https://api.frankfurter.app/latest?from=$from&to=$to"
            val url = URL(urlStr)

            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "GET"
                connectTimeout = 5000
                readTimeout = 5000

                if (responseCode == 200) {
                    inputStream.bufferedReader().use { reader ->
                        val json = JSONObject(reader.readText())
                        val rates = json.getJSONObject("rates")
                        val rate = rates.getDouble(to)

                        Log.d("RateX", "Rate fetched: $from → $to = $rate")
                        return rate
                    }
                } else {
                    Log.e("RateX", "HTTP error: $responseCode")
                }
            }
        } catch (e: Exception) {
            Log.e("RateX", "Error fetching rate", e)
        }
        return null
    }
}

