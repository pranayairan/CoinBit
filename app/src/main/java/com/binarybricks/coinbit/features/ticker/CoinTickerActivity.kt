package com.binarybricks.coinbit.features.ticker

import CoinTickerContract
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.widget.Toolbar
import android.view.View
import com.binarybricks.coinbit.CoinBitApplication
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.featurecomponents.cointickermodule.CoinTickerPresenter
import com.binarybricks.coinbit.featurecomponents.cointickermodule.CoinTickerRepository
import com.binarybricks.coinbit.data.PreferenceManager
import com.binarybricks.coinbit.network.models.CryptoTicker
import com.binarybricks.coinbit.network.schedulers.RxSchedulers
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManagerImpl
import com.binarybricks.coinbit.utils.openCustomTab
import com.crashlytics.android.Crashlytics
import kotlinx.android.synthetic.main.activity_coin_ticker_list.*
import java.util.*

/**
 * Created by Pranay Airan
 * Activity showing all ticker data
 */
class CoinTickerActivity : AppCompatActivity(), CoinTickerContract.View {

    companion object {
        private const val COIN_NAME = "COIN_FULL_NAME"

        @JvmStatic
        fun buildLaunchIntent(context: Context, coinName: String): Intent {
            val intent = Intent(context, CoinTickerActivity::class.java)
            intent.putExtra(COIN_NAME, coinName)
            return intent
        }
    }

    private val rxSchedulers: RxSchedulers by lazy {
        RxSchedulers.instance
    }

    private val coinTickerRepository by lazy {
        CoinTickerRepository(rxSchedulers, CoinBitApplication.database)
    }

    private val androidResourceManager by lazy {
        AndroidResourceManagerImpl(this)
    }

    private val coinTickerPresenter: CoinTickerPresenter by lazy {
        CoinTickerPresenter(rxSchedulers, coinTickerRepository, androidResourceManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coin_ticker_list)

        val toolbar = findViewById<View>(R.id.toolbar)
        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val coinName = intent.getStringExtra(COIN_NAME).trim()

        supportActionBar?.title = getString(R.string.tickerActivityTitle, coinName)

        rvCoinTickerList.layoutManager = LinearLayoutManager(this)

        coinTickerPresenter.attachView(this)

        lifecycle.addObserver(coinTickerPresenter)

        coinTickerPresenter.getCryptoTickers(coinName.toLowerCase())

        Crashlytics.log("CoinTickerActivity")
    }

    override fun showOrHideLoadingIndicator(showLoading: Boolean) {
        if (!showLoading) {
            pbLoading.hide()
        } else {
            pbLoading.show()
        }
    }

    override fun onPriceTickersLoaded(tickerData: List<CryptoTicker>) {
        val coinTickerAdapter = CoinTickerAdapter(tickerData,
                Currency.getInstance(PreferenceManager.getDefaultCurrency(this)), androidResourceManager)

        rvCoinTickerList.adapter = coinTickerAdapter

        tvFooter.setOnClickListener {
            openCustomTab(getString(R.string.coin_gecko_url), this)
        }
    }

    override fun onNetworkError(errorMessage: String) {
        Snackbar.make(rvCoinTickerList, errorMessage, Snackbar.LENGTH_LONG)
    }
}
