package com.binarybricks.coinbit.features.coinsearch

import CoinDiscoveryContract
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.binarybricks.coinbit.CoinBitApplication
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.featurecomponents.*
import com.binarybricks.coinbit.data.PreferenceManager
import com.binarybricks.coinbit.network.models.CoinPair
import com.binarybricks.coinbit.network.models.CoinPrice
import com.binarybricks.coinbit.network.models.CryptoCompareNews
import com.binarybricks.coinbit.network.schedulers.RxSchedulers
import com.binarybricks.coinbit.features.CryptoCompareRepository
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManager
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManagerImpl
import com.crashlytics.android.Crashlytics
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard.view.*

class CoinDiscoveryFragment : Fragment(), CoinDiscoveryContract.View {

    companion object {
        const val TAG = "CoinDiscoveryFragment"
    }

    private var nextMenuItem: MenuItem? = null

    private var coinDiscoveryList: ArrayList<ModuleItem> = ArrayList()
    private var coinDiscoveryAdapter: CoinDiscoveryAdapter? = null

    private val rxSchedulers: RxSchedulers by lazy {
        RxSchedulers.instance
    }

    private val androidResourceManager: AndroidResourceManager by lazy {
        AndroidResourceManagerImpl(requireContext())
    }

    private val coinRepo by lazy {
        CryptoCompareRepository(rxSchedulers, CoinBitApplication.database)
    }

    private val coinDiscoveryPresenter: CoinDiscoveryPresenter by lazy {
        CoinDiscoveryPresenter(rxSchedulers, coinRepo)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val inflate = inflater.inflate(R.layout.fragment_dashboard, container, false)

        val toolbar = inflate.toolbar
        toolbar?.title = getString(R.string.discover)

        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        initializeUI(inflate)

        coinDiscoveryPresenter.attachView(this)

        lifecycle.addObserver(coinDiscoveryPresenter)

        // empty existing list
        coinDiscoveryList = ArrayList()

        coinDiscoveryList.add(LabelModule.LabelModuleData(getString(R.string.top_volume)))
        coinDiscoveryList.add(CarousalModule.CarousalModuleData(null))

        coinDiscoveryList.add(LabelModule.LabelModuleData(getString(R.string.top_pair)))
        coinDiscoveryList.add(CarousalModule.CarousalModuleData(null))

        coinDiscoveryList.add(LabelModule.LabelModuleData(getString(R.string.recent_news)))

        // coinDashboardList.add(0, CarousalModule.CarousalModuleData(null))

        coinDiscoveryAdapter?.coinDiscoverList = coinDiscoveryList
        coinDiscoveryAdapter?.notifyDataSetChanged()

        // get top coin by market cap
        coinDiscoveryPresenter.getTopCoinListByMarketCap(PreferenceManager.getDefaultCurrency(context))

        // get coins by volume
        coinDiscoveryPresenter.getTopCoinListByPairVolume()

        // get news
        coinDiscoveryPresenter.getCryptoCurrencyNews()

        setHasOptionsMenu(true)

        Crashlytics.log("CoinDiscoveryFragment")

        return inflate
    }

    private fun initializeUI(inflatedView: View) {

        inflatedView.rvDashboard.layoutManager = LinearLayoutManager(context)

        coinDiscoveryAdapter = CoinDiscoveryAdapter(PreferenceManager.getDefaultCurrency(context), androidResourceManager,
                coinDiscoveryList)

        inflatedView.rvDashboard.adapter = coinDiscoveryAdapter
    }

    override fun onTopCoinsByTotalVolumeLoaded(topCoins: List<CoinPrice>) {

        val topCardList = mutableListOf<TopCardModule.TopCardsModuleData>()
        topCoins.forEach {
            topCardList.add(TopCardModule.TopCardsModuleData("${it.fromSymbol}/${it.toSymbol}", it.price
                    ?: "0", it.changePercentage24Hour ?: "0", it.marketCap ?: "0",
                    it.fromSymbol ?: ""))
        }

        coinDiscoveryAdapter?.let {
            if (!it.coinDiscoverList.isNullOrEmpty()) {
                it.coinDiscoverList[1] = CarousalModule.CarousalModuleData(topCardList)
                coinDiscoveryAdapter?.notifyItemChanged(1)
            }
        }
    }

    override fun onTopCoinListByPairVolumeLoaded(topPair: List<CoinPair>) {
        coinDiscoveryAdapter?.let {
            if (!it.coinDiscoverList.isNullOrEmpty()) {
                it.coinDiscoverList[3] = CarousalModule.CarousalModuleData(listOf(ChipGroupModule.ChipGroupModuleData(topPair)))
                coinDiscoveryAdapter?.notifyItemChanged(3)
            }
        }
    }

    override fun onCoinNewsLoaded(coinNews: List<CryptoCompareNews>) {
        coinDiscoveryAdapter?.let {
            val previousSize = it.coinDiscoverList.size
            coinNews.forEach { news ->
                it.coinDiscoverList.add(DiscoveryNewsModule.DiscoveryNewsModuleData(news))
            }
            it.notifyItemRangeChanged(previousSize, it.coinDiscoverList.size)
        }
    }

    override fun onNetworkError(errorMessage: String) {
        Snackbar.make(rvDashboard, errorMessage, Snackbar.LENGTH_LONG).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.home_menu, menu)

        nextMenuItem = menu?.findItem(R.id.action_search)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_search -> {
                context?.let {
                    startActivity(CoinSearchActivity.buildLaunchIntent(it))
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}