package com.binarybricks.coinbit.features.transaction

import CoinTransactionContract
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.binarybricks.coinbit.CoinBitApplication
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.data.PreferenceManager
import com.binarybricks.coinbit.data.database.entities.Coin
import com.binarybricks.coinbit.data.database.entities.CoinTransaction
import com.binarybricks.coinbit.features.CryptoCompareRepository
import com.binarybricks.coinbit.features.exchangesearch.ExchangeSearchActivity
import com.binarybricks.coinbit.features.pairsearch.PairSearchActivity
import com.binarybricks.coinbit.network.models.ExchangePair
import com.binarybricks.coinbit.utils.Formaters
import com.binarybricks.coinbit.utils.TRANSACTION_TYPE_BUY
import com.binarybricks.coinbit.utils.dismissKeyboard
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManagerImpl
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.activity_coin_transaction.*
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.util.*

class CoinTransactionActivity : AppCompatActivity(), CoinTransactionContract.View, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private var exchangeName = ""
    private var pairName = ""

    private val transactionDate by lazy {
        Calendar.getInstance()
    }

    private val androidResourceManager by lazy {
        AndroidResourceManagerImpl(this)
    }
    private val formatter by lazy {
        Formaters(androidResourceManager)
    }

    private val coinRepo by lazy {
        CryptoCompareRepository(CoinBitApplication.database)
    }

    private val coinTransactionPresenter: CoinTransactionPresenter by lazy {
        CoinTransactionPresenter(coinRepo)
    }

    private val defaultCurrency: String by lazy {
        PreferenceManager.getDefaultCurrency(this)
    }

    private val mc: MathContext by lazy {
        MathContext(6, RoundingMode.HALF_UP)
    }

    private var exchangeCoinMap: HashMap<String, MutableList<ExchangePair>>? = null

    private var coin: Coin? = null
    private var cost = BigDecimal.ZERO
    private var buyPrice = BigDecimal.ZERO
    private var buyPriceInHomeCurrency = BigDecimal.ZERO
    private var prices: MutableMap<String, BigDecimal> = hashMapOf()

    private val transactionType = TRANSACTION_TYPE_BUY

    companion object {
        private const val COIN = "COIN"
        private const val EXCHANGE_REQUEST = 100
        private const val PAIR_REQUEST = 101

        @JvmStatic
        fun buildLaunchIntent(context: Context, coin: Coin): Intent {
            val intent = Intent(context, CoinTransactionActivity::class.java)
            intent.putExtra(COIN, coin)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coin_transaction)

        val toolbar = findViewById<View>(R.id.toolbar)
        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        coin = intent.getParcelableExtra(COIN)

        checkNotNull(coin)

        supportActionBar?.title = coin?.fullName

        coinTransactionPresenter.attachView(this)
        lifecycle.addObserver(coinTransactionPresenter)

        initializeUI()

        coinTransactionPresenter.getAllSupportedExchanges()

        FirebaseCrashlytics.getInstance().log("CoinTransactionActivity")
    }

    private fun initializeUI() {

        svContainer.setOnClickListener {
            dismissKeyboard(this)
        }

        containerExchange.setOnClickListener {
            val exchangeList = exchangeCoinMap?.get(coin?.symbol?.toUpperCase())
            if (exchangeList != null) {

                startActivityForResult(
                    ExchangeSearchActivity.buildLaunchIntent(this, getExchangeNameList(exchangeList), getString(R.string.change_exchange)),
                    EXCHANGE_REQUEST
                )
            }
        }

        containerPair.setOnClickListener {
            val symbol = coin?.symbol

            val exchangeList = exchangeCoinMap?.get(symbol?.toUpperCase())
            if (exchangeList != null && symbol != null && exchangeName.isNotEmpty()) {
                startActivityForResult(
                    PairSearchActivity.buildLaunchIntent(this, getTopPair(exchangeList), symbol),
                    PAIR_REQUEST
                )
            }
        }

        containerDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog.newInstance(
                this, transactionDate.get(Calendar.YEAR), transactionDate.get(Calendar.MONTH),
                transactionDate.get(Calendar.DAY_OF_MONTH)
            )

            datePickerDialog.isThemeDark = true
            datePickerDialog.accentColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
            datePickerDialog.show(supportFragmentManager, "DatePickerDialog")
        }

        etBuyPrice.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                if (editable.isNullOrEmpty()) {
                    tvBuyPriceLabel.visibility = View.GONE
                } else {
                    tvBuyPriceLabel.visibility = View.VISIBLE
                    calculateCost()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        etAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                if (editable.isNullOrEmpty()) {
                    tvBuyAmountLabel.visibility = View.GONE
                } else {
                    tvBuyAmountLabel.visibility = View.VISIBLE
                    calculateCost()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        btnAddTransaction.setOnClickListener {
            val coinTransaction = validateAndMakeTransaction()
            if (coinTransaction != null) {
                loading.show()
                coinTransactionPresenter.addTransaction(coinTransaction)
            }
        }
    }

    override fun onAllSupportedExchangesLoaded(exchangeCoinMap: HashMap<String, MutableList<ExchangePair>>) {
        this.exchangeCoinMap = exchangeCoinMap
        // check for default exchange chosen
    }

    override fun onCoinPriceLoaded(prices: MutableMap<String, BigDecimal>) {
        etBuyPrice.setText(prices[pairName.toUpperCase()].toString())
        this.prices = prices
    }

    override fun onTransactionAdded() {
        loading.hide()
        finish()
    }

    private fun calculateCost() {
        if (etBuyPrice.text.isNotEmpty() && etAmount.text.isNotEmpty()) {
            buyPrice = BigDecimal(etBuyPrice.text.toString())
            buyPriceInHomeCurrency = buyPrice

            // this means the pair is not home currency one
            if (prices.size > 1 && prices.containsKey(defaultCurrency.toUpperCase())) {
                // get rate
                val rate = BigDecimal(etBuyPrice.text.toString()).divide(prices[pairName.toUpperCase()], mc)
                buyPriceInHomeCurrency = (prices[defaultCurrency.toUpperCase()]?.multiply(rate, mc))

                // cal cost
                cost = buyPriceInHomeCurrency.multiply(BigDecimal(etAmount.text.toString()), mc)
                tvTotalAmountInCurrencyLabel.text = getString(R.string.transactionCost, cost, defaultCurrency.toUpperCase())
            } else {
                cost = buyPrice.multiply(BigDecimal(etAmount.text.toString()), mc)
                tvTotalAmountInCurrencyLabel.text = getString(R.string.transactionCost, cost, pairName)
            }
        }
    }

    private fun validateAndMakeTransaction(): CoinTransaction? {
        calculateCost()

        coin?.let {
            if (pairName.isNotEmpty() && buyPrice > BigDecimal.ZERO && buyPriceInHomeCurrency > BigDecimal.ZERO &&
                etAmount.text.isNotEmpty() && cost > BigDecimal.ZERO
            ) {
                return CoinTransaction(
                    transactionType, it.symbol, pairName, buyPrice, buyPriceInHomeCurrency, BigDecimal(etAmount.text.toString()),
                    transactionDate.timeInMillis.toString(), cost.toPlainString(), exchangeName, BigDecimal.ZERO
                )
            }
        }
        return null
    }

    private fun getExchangeNameList(exchangePairList: MutableList<ExchangePair>): ArrayList<String> {
        val exchangeList: ArrayList<String> = arrayListOf()

        exchangePairList.forEach {
            exchangeList.add(it.exchangeName)
        }

        exchangeList.sort()
        return exchangeList
    }

    private fun getTopPair(exchangePairList: MutableList<ExchangePair>): ArrayList<String> {
        exchangePairList.filter {
            it.exchangeName.equals(exchangeName, true)
        }.map { exchangePair ->
            return exchangePair.pairList as ArrayList<String>
        }

        return arrayListOf()
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        transactionDate.set(Calendar.YEAR, year)
        transactionDate.set(Calendar.MONTH, monthOfYear)
        transactionDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        val timePickerDialog = TimePickerDialog.newInstance(
            this, transactionDate.get(Calendar.HOUR_OF_DAY), transactionDate.get(Calendar.MINUTE),
            transactionDate.get(Calendar.SECOND), false
        )

        timePickerDialog.accentColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        timePickerDialog.isThemeDark = true
        timePickerDialog.show(supportFragmentManager, "TimePickerDialog")
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        transactionDate.set(Calendar.HOUR_OF_DAY, hourOfDay)
        transactionDate.set(Calendar.MINUTE, minute)
        transactionDate.set(Calendar.SECOND, second)

        tvDatetimeLabel.visibility = View.VISIBLE
        tvDatetime.text = formatter.formatDatePretty(transactionDate.time)

        getCoinPrice()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            EXCHANGE_REQUEST -> {
                if (data != null) {
                    exchangeName = ExchangeSearchActivity.getResultFromIntent(data)
                    tvExchangeLabel.visibility = View.VISIBLE
                    tvExchange.text = exchangeName.toUpperCase()

                    tvPair.text = getString(R.string.trading_pair)
                    pairName = ""
                    etBuyPrice.setText("")
                    etAmount.setText("")
                    tvTotalAmountInCurrencyLabel.text = ""
                }
            }
            PAIR_REQUEST -> {
                if (data != null) {
                    pairName = PairSearchActivity.getResultFromIntent(data)
                    tvPairLabel.visibility = View.VISIBLE

                    if (coin != null) {
                        tvPair.text = getString(
                            R.string.coinPair,
                            coin?.symbol, pairName.toUpperCase()
                        )

                        getCoinPrice()
                    }

                    tvBuyPriceLabel.text = getString(R.string.buyPriceHint, pairName)
                    etBuyPrice.hint = getString(R.string.buyPriceHint, pairName)
                    etAmount.setText("")
                    tvTotalAmountInCurrencyLabel.text = ""
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun getCoinPrice() {

        var toCurrencies = pairName

        // in case pair is not default currency get default currency as well say for example if pair is ETH/BTC get price in USD as well
        if (!pairName.contains(defaultCurrency, true)) {
            toCurrencies = "$toCurrencies,$defaultCurrency"
        }

        coinTransactionPresenter.getPriceForPair(
            coin?.symbol ?: "",
            toCurrencies, exchangeName, (transactionDate.timeInMillis / 1000).toInt().toString()
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            // Respond to the action bar's Up/Home button
            android.R.id.home -> {
                // tell the calling activity/fragment that we're done deleting this transaction
                onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNetworkError(errorMessage: String) {
        Snackbar.make(svContainer, errorMessage, Snackbar.LENGTH_LONG).show()
    }
}
