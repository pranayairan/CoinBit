import com.binarybricks.coinbit.network.models.CoinPair
import com.binarybricks.coinbit.network.models.CoinPrice
import com.binarybricks.coinbit.network.models.CryptoCompareNews
import com.binarybricks.coinbit.features.BaseView

/**
Created by Pranay Airan
 */

interface CoinDiscoveryContract {

    interface View : BaseView {
        fun onTopCoinsByTotalVolumeLoaded(topCoins: List<CoinPrice>)
        fun onTopCoinListByPairVolumeLoaded(topPair: List<CoinPair>)
        fun onCoinNewsLoaded(coinNews: List<CryptoCompareNews>)
    }

    interface Presenter {
        fun getTopCoinListByMarketCap(toCurrencySymbol: String)
        fun getTopCoinListByPairVolume()
        fun getCryptoCurrencyNews()
    }
}