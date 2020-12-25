package com.binarybricks.coinbit.epoxymodels

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import coil.load
import coil.transform.CircleCropTransformation
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.data.database.entities.WatchedCoin
import com.binarybricks.coinbit.network.BASE_CRYPTOCOMPARE_IMAGE_URL
import java.math.BigDecimal

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class CoinSearchItemView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0,
) : ConstraintLayout(context, attributeSet, defStyle) {

    private val tvCoinName: TextView
    private val tvCoinSymbol: TextView
    private val ivCoin: ImageView
    private val cbWatched: SwitchCompat
    private val clCoinInfo: View

    private val cropCircleTransformation by lazy {
        CircleCropTransformation()
    }

    interface OnSearchItemClickListener {
        fun onItemWatchedClicked(watched: Boolean)
    }

    init {
        View.inflate(context, R.layout.coin_search_item, this)
        tvCoinName = findViewById(R.id.tvCoinPercentChange)
        tvCoinSymbol = findViewById(R.id.tvCoinName)
        ivCoin = findViewById(R.id.ivCoin)
        cbWatched = findViewById(R.id.scWatched)
        clCoinInfo = findViewById(R.id.clCoinInfo)
    }

    @ModelProp
    fun setWatchedCoin(watchedCoin: WatchedCoin) {
        tvCoinName.text = watchedCoin.coin.coinName
        tvCoinSymbol.text = watchedCoin.coin.symbol

        ivCoin.load(BASE_CRYPTOCOMPARE_IMAGE_URL + "${watchedCoin.coin.imageUrl}?width=50") {
            crossfade(true)
            error(R.mipmap.ic_launcher_round)
            transformations(cropCircleTransformation)
        }

        val purchaseQuantity = watchedCoin.purchaseQuantity

        cbWatched.isChecked = purchaseQuantity > BigDecimal.ZERO || watchedCoin.watched
    }

    @CallbackProp
    fun setItemClickListener(listener: OnClickListener?) {
        clCoinInfo.setOnClickListener(listener)
    }

    @CallbackProp
    fun setOnWatchedChecked(listener: OnSearchItemClickListener?) {
        cbWatched.setOnClickListener {
            listener?.onItemWatchedClicked(cbWatched.isChecked)
        }
    }
}
