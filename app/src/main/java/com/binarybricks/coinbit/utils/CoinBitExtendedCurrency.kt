package com.binarybricks.coinbit.utils

import com.mynameismidori.currencypicker.ExtendedCurrency
import com.mynameismidori.currencypicker.R
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

class CoinBitExtendedCurrency {

    companion object {
        val CURRENCIES = listOf(
            ExtendedCurrency("EUR", "Euro", "€", R.drawable.flag_eur),
            ExtendedCurrency("USD", "United States Dollar", "$", R.drawable.flag_usd),
            ExtendedCurrency("GBP", "British Pound", "£", R.drawable.flag_gbp),
            ExtendedCurrency("CZK", "Czech Koruna", "Kč", R.drawable.flag_czk),
            ExtendedCurrency("TRY", "Turkish Lira", "₺", R.drawable.flag_try),
            ExtendedCurrency("AED", "Emirati Dirham", "د.إ", R.drawable.flag_aed),
            ExtendedCurrency("AUD", "Australian Dollar", "$", R.drawable.flag_aud),
            ExtendedCurrency("BRL", "Brazil Real", "R$", R.drawable.flag_brl),
            ExtendedCurrency("CAD", "Canada Dollar", "$", R.drawable.flag_cad),
            ExtendedCurrency("CHF", "Switzerland Franc", "CHF", R.drawable.flag_chf),
            ExtendedCurrency("CNY", "China Yuan Renminbi", "¥", R.drawable.flag_cny),
            ExtendedCurrency("DKK", "Denmark Krone", "kr", R.drawable.flag_dkk),
            ExtendedCurrency("GHS", "Ghana Cedi", "¢", R.drawable.flag_ghs),
            ExtendedCurrency("HKD", "Hong Kong Dollar", "$", R.drawable.flag_hkd),
            ExtendedCurrency("IDR", "Indonesia Rupiah", "Rp", R.drawable.flag_idr),
            ExtendedCurrency("ILS", "Israel Shekel", "₪", R.drawable.flag_ils),
            ExtendedCurrency("JPY", "Japanese Yen", "¥", R.drawable.flag_jpy),
            ExtendedCurrency("KES", "Kenyan Shilling", "KSh", R.drawable.flag_kes),
            ExtendedCurrency("KRW", "Korea (South) Won", "₩", R.drawable.flag_krw),
            ExtendedCurrency("MXN", "Mexico Peso", "$", R.drawable.flag_mxn),
            ExtendedCurrency("MYR", "Malaysia Ringgit", "RM", R.drawable.flag_myr),
            ExtendedCurrency("NGN", "Nigeria Naira", "₦", R.drawable.flag_ngn),
            ExtendedCurrency("NOK", "Norway Krone", "kr", R.drawable.flag_nok),
            ExtendedCurrency("PKR", "Pakistan Rupee", "₨", R.drawable.flag_pkr),
            ExtendedCurrency("PLN", "Poland Zloty", "zł", R.drawable.flag_pln),
            ExtendedCurrency("RUB", "Russia Ruble", "₽", R.drawable.flag_rub),
            ExtendedCurrency("SEK", "Sweden Krona", "kr", R.drawable.flag_sek),
            ExtendedCurrency("SGD", "Singapore Dollar", "$", R.drawable.flag_sgd),
            ExtendedCurrency("THB", "Thailand Baht", "฿", R.drawable.flag_thb),
            ExtendedCurrency("UAH", "Ukraine Hryvnia", "₴", R.drawable.flag_uah),
            ExtendedCurrency("ZAR", "South Africa Rand", "R", R.drawable.flag_zar)
        )
        private val TRILLION = BigDecimal(1000000000000)
        private val BILLION = BigDecimal(1000000000)
        private val MILLION = BigDecimal(1000000)
        private val THOUSANDS = BigDecimal(10000)

        /**
         * Method to give text for the amount passed. This method
         * will reduce the amount to 2 digits. Say if we pass 1300000
         * it will return 1.3 M representing 1.3 million
         */
        fun getAmountTextForDisplay(amount: BigDecimal, amountCurrency: Currency): String {

            var df = DecimalFormat("\u00a4###,###.##;-\u00a4###,###.##")
            df.currency = amountCurrency
            df.roundingMode = RoundingMode.HALF_UP

            var textAmount = amount

            if (amount.compareTo(TRILLION) != -1) {
                df = DecimalFormat("\u00a4###,### T;-\u00a4###,### T")
                textAmount = textAmount.divide(TRILLION, RoundingMode.HALF_UP)
            } else if (amount.compareTo(BILLION) != -1) {
                df = DecimalFormat("\u00a4###,### B;-\u00a4###,### B")
                textAmount = textAmount.divide(BILLION, RoundingMode.HALF_UP)
            } else if (amount.compareTo(MILLION) != -1) {
                df = DecimalFormat("\u00a4###,### M;-\u00a4###,### M")
                textAmount = textAmount.divide(MILLION, RoundingMode.HALF_UP)
            } else if (amount.compareTo(THOUSANDS) != -1) {
                df = DecimalFormat("\u00a4###,### K;-\u00a4###,### K")
                textAmount = textAmount.divide(THOUSANDS, RoundingMode.HALF_UP)
            }

            return df.format(textAmount)
        }
    }
}
