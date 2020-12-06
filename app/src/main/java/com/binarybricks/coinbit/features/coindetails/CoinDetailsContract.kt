import com.binarybricks.coinbit.data.database.entities.WatchedCoin
import com.binarybricks.coinbit.features.BaseView

/**
Created by Pranay Airan
 */

interface CoinDetailsContract {

    interface View : BaseView {
        fun showOrHideLoadingIndicator(showLoading: Boolean = true)
        fun onWatchedCoinLoaded(coin: WatchedCoin?)
    }

    interface Presenter {
        fun getWatchedCoinFromSymbol(symbol: String)
    }
}
