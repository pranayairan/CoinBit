package com.binarybricks.coinbit.features.coindetails

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.binarybricks.coinbit.data.database.entities.WatchedCoin
import com.binarybricks.coinbit.features.coin.CoinFragment

/**
 * Created by pranay airan on 2/11/18.
 */

class CoinDetailsPagerAdapter(private val watchedCoinList: List<WatchedCoin>?, fm: FragmentManager?) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment? {
        if (watchedCoinList != null) {
            val coinDetailsFragment = CoinFragment()
            coinDetailsFragment.arguments = CoinFragment.getArgumentBundle(watchedCoinList[position])
            return coinDetailsFragment
        }
        return null
    }

    override fun getCount(): Int {
        return watchedCoinList?.size ?: 0
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return watchedCoinList?.get(position)?.coin?.symbol ?: super.getPageTitle(position)
    }
}