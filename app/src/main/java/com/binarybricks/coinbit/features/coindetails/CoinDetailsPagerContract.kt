import com.binarybricks.coinbit.data.database.entities.WatchedCoin
import com.binarybricks.coinbit.features.BaseView

/**
Created by Pranay Airan
 */

interface CoinDetailsPagerContract {

    interface View : BaseView {
        fun onWatchedCoinsLoaded(watchedCoinList: List<WatchedCoin>?)
    }

    interface Presenter {
        fun loadWatchedCoins()
    }
}