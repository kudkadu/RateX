package com.example.ratex.model
import androidx.annotation.StringRes
import com.example.ratex.R

enum class Currency(val code: String, @StringRes val labelId: Int) {
    USD("USD", R.string.usd_label),
    EUR("EUR", R.string.eur_label),
    GBP("GBP", R.string.gbp_label),
    JPY("JPY", R.string.jpy_label),
    CNY("CNY", R.string.cny_label); // Заменил RUB на CNY
}
