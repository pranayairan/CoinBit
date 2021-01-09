package com.binarybricks.coinbit.features.coindetails

import CoinDetailsContract
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.binarybricks.coinbit.CoinBitApplication
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.data.database.entities.WatchedCoin
import com.binarybricks.coinbit.features.CryptoCompareRepository
import com.binarybricks.coinbit.features.coin.CoinFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.activity_coin_details.*

class CoinDetailsActivity : AppCompatActivity(), CoinDetailsContract.View {

    companion object {
        private const val WATCHED_COIN = "WATCHED_COIN"
        private const val COIN_SYMBOL = "COIN_SYMBOL"

        @JvmStatic
        fun buildLaunchIntent(context: Context, watchedCoin: WatchedCoin): Intent {
            val intent = Intent(context, CoinDetailsActivity::class.java)
            intent.putExtra(WATCHED_COIN, watchedCoin)
            return intent
        }

        @JvmStatic
        fun buildLaunchIntent(context: Context, symbol: String): Intent {
            val intent = Intent(context, CoinDetailsActivity::class.java)
            intent.putExtra(COIN_SYMBOL, symbol)
            return intent
        }
    }

    private val coinRepo by lazy {
        CryptoCompareRepository(CoinBitApplication.database)
    }

    private val coinDetailPresenter: CoinDetailPresenter by lazy {
        CoinDetailPresenter(coinRepo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coin_details)

        val toolbar = findViewById<View>(R.id.toolbar)
        setSupportActionBar(toolbar as Toolbar?)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.elevation = 0f

        coinDetailPresenter.attachView(this)

        lifecycle.addObserver(coinDetailPresenter)

        val watchedCoin: WatchedCoin? = intent.getParcelableExtra(WATCHED_COIN)

        if (watchedCoin != null) {
            onWatchedCoinLoaded(watchedCoin)
        } else {
            intent.getStringExtra(COIN_SYMBOL)?.let {
                coinDetailPresenter.getWatchedCoinFromSymbol(it)
            }
        }

        FirebaseCrashlytics.getInstance().log("CoinDetailsActivity")
    }

    override fun onWatchedCoinLoaded(coin: WatchedCoin?) {
        if (coin != null) {
            showOrHideLoadingIndicator(false)

            val coinDetailsFragment = CoinFragment()
            coinDetailsFragment.arguments = CoinFragment.getArgumentBundle(coin)

            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.flCoinDetails, coinDetailsFragment)
            fragmentTransaction.commit()

            supportActionBar?.title = getString(
                R.string.transactionTypeWithQuantity,
                coin.coin.coinName, coin.coin.symbol
            )
        }
    }

    override fun showOrHideLoadingIndicator(showLoading: Boolean) {
        if (!showLoading) {
            pbLoading.hide()
        } else {
            pbLoading.show()
        }
    }

    override fun onNetworkError(errorMessage: String) {
        Snackbar.make(flCoinDetails, errorMessage, Snackbar.LENGTH_LONG)
    }
}
