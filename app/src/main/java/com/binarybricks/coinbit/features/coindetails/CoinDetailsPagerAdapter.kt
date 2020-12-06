package com.binarybricks.coinbit.features.coindetails

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.binarybricks.coinbit.data.database.entities.WatchedCoin
import com.binarybricks.coinbit.features.coin.CoinFragment

/**
 * Created by pranay airan on 2/11/18.
 */

class CoinDetailsPagerAdapter(private val watchedCoinList: List<WatchedCoin>?, fm: FragmentManager) : FragmentStatePagerAdapter(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        val coinDetailsFragment = CoinFragment()
        if (watchedCoinList != null) {
            coinDetailsFragment.arguments = CoinFragment.getArgumentBundle(watchedCoinList[position])
            return coinDetailsFragment
        }
        return coinDetailsFragment
    }

    override fun getCount(): Int {
        return watchedCoinList?.size ?: 0
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return watchedCoinList?.get(position)?.coin?.symbol ?: super.getPageTitle(position)
    }
}
