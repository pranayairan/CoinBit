package com.binarybricks.coinbit.features.dashboard

import CoinDashboardContract
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.carousel
import com.binarybricks.coinbit.CoinBitApplication
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.data.CoinBitCache
import com.binarybricks.coinbit.data.PreferenceManager
import com.binarybricks.coinbit.data.database.entities.CoinTransaction
import com.binarybricks.coinbit.data.database.entities.WatchedCoin
import com.binarybricks.coinbit.epoxymodels.*
import com.binarybricks.coinbit.featurecomponents.ModuleItem
import com.binarybricks.coinbit.features.CryptoCompareRepository
import com.binarybricks.coinbit.features.coindetails.CoinDetailsActivity
import com.binarybricks.coinbit.features.coindetails.CoinDetailsPagerActivity
import com.binarybricks.coinbit.features.coinsearch.CoinSearchActivity
import com.binarybricks.coinbit.network.models.CoinPrice
import com.binarybricks.coinbit.network.models.CryptoCompareNews
import com.binarybricks.coinbit.network.schedulers.RxSchedulers
import com.binarybricks.coinbit.utils.openCustomTab
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManager
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManagerImpl
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import java.util.*
import kotlin.collections.ArrayList

class CoinDashboardFragment : Fragment(), CoinDashboardContract.View {

    companion object {
        const val TAG = "CoinDashboardFragment"
        private const val COIN_SEARCH_CODE = 100
        private const val COIN_DETAILS_CODE = 101
    }

    private var coinDashboardList: MutableList<ModuleItem> = ArrayList()
    private var watchedCoinList: List<WatchedCoin> = emptyList()
    private var coinTransactionList: List<CoinTransaction> = emptyList()

    private val rxSchedulers: RxSchedulers by lazy {
        RxSchedulers.instance
    }

    private val androidResourceManager: AndroidResourceManager by lazy {
        AndroidResourceManagerImpl(requireContext())
    }

    private val dashboardRepository by lazy {
        DashboardRepository(rxSchedulers, CoinBitApplication.database)
    }

    private val coinRepo by lazy {
        CryptoCompareRepository(rxSchedulers, CoinBitApplication.database)
    }

    private val coinDashboardPresenter: CoinDashboardPresenter by lazy {
        CoinDashboardPresenter(rxSchedulers, dashboardRepository, coinRepo)
    }

    private val coinNews: MutableList<CryptoCompareNews> = mutableListOf()

    private lateinit var rvDashboard: EpoxyRecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val inflate = inflater.inflate(R.layout.fragment_dashboard, container, false)

        val toolbar = inflate.toolbar
        toolbar?.title = getString(R.string.market)

        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        coinDashboardPresenter.attachView(this)

        lifecycle.addObserver(coinDashboardPresenter)

        // empty existing list
        coinDashboardList = ArrayList()

        initializeUI(inflate)

        // get top coins
        coinDashboardPresenter.getTopCoinsByTotalVolume24hours(PreferenceManager.getDefaultCurrency(context))

        // get prices for watched coin
        coinDashboardPresenter.loadWatchedCoinsAndTransactions()

        FirebaseCrashlytics.getInstance().log("CoinDashboardFragment")

        return inflate
    }

    private fun initializeUI(inflatedView: View) {

        rvDashboard = inflatedView.rvDashboard

        inflatedView.swipeContainer.setOnRefreshListener {
            coinDashboardList.clear()

            // get top coins
            coinDashboardPresenter.getTopCoinsByTotalVolume24hours(PreferenceManager.getDefaultCurrency(context))

            coinDashboardPresenter.loadWatchedCoinsAndTransactions()

            inflatedView.swipeContainer.isRefreshing = false
        }
    }

    override fun onWatchedCoinsAndTransactionsLoaded(watchedCoinList: List<WatchedCoin>, coinTransactionList: List<CoinTransaction>) {

        this.watchedCoinList = watchedCoinList
        this.coinTransactionList = coinTransactionList

        setupDashBoardAdapter(watchedCoinList, coinTransactionList)

        getAllWatchedCoinsPrice()
    }

    private fun setupDashBoardAdapter(watchedCoinList: List<WatchedCoin>, coinTransactionList: List<CoinTransaction>) {

        watchedCoinList.forEach { watchedCoin ->
            coinDashboardList.add(
                CoinItemView.DashboardCoinModuleData(
                    false, watchedCoin,
                    null, coinTransactionList
                )
            )
        }

        coinDashboardList.add(AddCoinItemView.AddCoinModuleItem)

        coinDashboardList.add(GenericFooterItemView.FooterModuleData(getString(R.string.crypto_compare), getString(R.string.crypto_compare_url)))

        showDashboardData(coinDashboardList)
    }

    private fun getAllWatchedCoinsPrice() {

        // get news
        coinDashboardPresenter.getLatestNewsFromCryptoCompare()

        // cryptocompare support only 100 coins in 1 shot. For safety we will support 95 and paginate
        val chunkWatchedList: List<List<WatchedCoin>> = watchedCoinList.chunked(95)

        chunkWatchedList.forEach {
            // we have all the watched coins now get price for all the coins
            var fromSymbol = ""
            it.forEachIndexed { index, watchedCoin ->
                if (index != it.size - 1) {
                    fromSymbol = fromSymbol + watchedCoin.coin.symbol + ","
                } else {
                    fromSymbol += watchedCoin.coin.symbol
                }
            }
            coinDashboardPresenter.loadCoinsPrices(fromSymbol, PreferenceManager.getDefaultCurrency(context))
        }
    }

    override fun onCoinPricesLoaded(coinPriceListMap: HashMap<String, CoinPrice>) {

        coinDashboardList.forEachIndexed { index, item ->
            if (item is CoinItemView.DashboardCoinModuleData && coinPriceListMap.contains(item.watchedCoin.coin.symbol.toUpperCase())) {
                coinDashboardList[index] = item.copy(coinPrice = coinPriceListMap[item.watchedCoin.coin.symbol.toUpperCase()])
            } else if (item is DashboardHeaderItemView.DashboardHeaderModuleData) {
                coinDashboardList[index] = item.copy(coinPriceListMap = coinPriceListMap)
            }
        }

        // update dashboard card
        showDashboardData(coinDashboardList)
    }

    override fun onTopCoinsByTotalVolumeLoaded(topCoins: List<CoinPrice>) {

        val topCardList = mutableListOf<TopCardItemView.TopCardsModuleData>()
        topCoins.forEach {
            topCardList.add(
                TopCardItemView.TopCardsModuleData(
                    "${it.fromSymbol}/${it.toSymbol}",
                    it.price
                        ?: "0",
                    it.changePercentage24Hour ?: "0", it.marketCap ?: "0",
                    it.fromSymbol ?: ""
                )
            )
        }
        coinDashboardList.add(0, TopCardList(topCardList))
        showDashboardData(coinDashboardList)
    }

    private data class TopCardList(val topCardList: List<TopCardItemView.TopCardsModuleData>) : ModuleItem

    override fun onCoinNewsLoaded(coinNews: List<CryptoCompareNews>) {
        this.coinNews.clear()
        this.coinNews.addAll(coinNews)

        if (coinDashboardList.size > 0) {
            if (coinDashboardList[1] is ShortNewsItemView.ShortNewsModuleData) {
                coinDashboardList[1] = ShortNewsItemView.ShortNewsModuleData(coinNews[0])
            } else {
                coinDashboardList.add(1, ShortNewsItemView.ShortNewsModuleData(coinNews[0]))
            }
        } else {
            coinDashboardList.add(ShortNewsItemView.ShortNewsModuleData(coinNews[0]))
        }
        showDashboardData(coinDashboardList)
    }

    private fun showDashboardData(coinList: List<ModuleItem>) {
        rvDashboard.withModels {
            coinList.forEachIndexed { index, moduleItem ->
                when (moduleItem) {
                    is TopCardList -> {
                        val topCards = mutableListOf<TopCardItemViewModel_>()
                        moduleItem.topCardList.forEach {
                            topCards.add(
                                TopCardItemViewModel_()
                                    .id(it.pair).topCardData(it).itemClickListener(object : TopCardItemView.OnTopItemClickedListener {
                                        override fun onItemClicked(coinSymbol: String) {
                                            context?.startActivity(
                                                CoinDetailsActivity.buildLaunchIntent(requireContext(), coinSymbol)
                                            )
                                        }
                                    })
                            )
                        }
                        carousel {
                            id("topCardList")
                            models(topCards)
                            numViewsToShowOnScreen(2.5F)
                            Carousel.setDefaultGlobalSnapHelperFactory(null)
                        }
                    }
                    is ShortNewsItemView.ShortNewsModuleData -> shortNewsItemView {
                        id("shortNews")
                        newsDate(moduleItem.news.title ?: "")
                        itemClickListener { _ ->
                            moduleItem.news.url?.let {
                                openCustomTab(it, requireContext())
                                CoinBitCache.updateCryptoCompareNews(moduleItem.news)
                                coinDashboardPresenter.getLatestNewsFromCryptoCompare()
                            }
                        }
                    }
                    is CoinItemView.DashboardCoinModuleData -> coinItemView {
                        id(moduleItem.watchedCoin.coin.id)
                        dashboardCoinModuleData(moduleItem)
                        itemClickListener(object : CoinItemView.OnCoinItemClickListener {
                            override fun onCoinClicked(watchedCoin: WatchedCoin) {
                                startActivityForResult(CoinDetailsPagerActivity.buildLaunchIntent(requireContext(), watchedCoin), COIN_DETAILS_CODE)
                            }
                        })
                    }
                    is AddCoinItemView.AddCoinModuleItem -> addCoinItemView {
                        id("add coin")
                        addCoinClickListener { _ ->
                            startActivityForResult(CoinSearchActivity.buildLaunchIntent(requireContext()), COIN_SEARCH_CODE)
                        }
                    }
                    is GenericFooterItemView.FooterModuleData -> genericFooterItemView {
                        id("footer")
                        footerContent(moduleItem)
                    }
                }
            }
        }
    }

    override fun onNetworkError(errorMessage: String) {
        Snackbar.make(rvDashboard, errorMessage, Snackbar.LENGTH_LONG).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (COIN_SEARCH_CODE == requestCode || COIN_DETAILS_CODE == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                coinDashboardPresenter.loadWatchedCoinsAndTransactions()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroyView() {
        (activity as AppCompatActivity).setSupportActionBar(null)
        super.onDestroyView()
    }
}
