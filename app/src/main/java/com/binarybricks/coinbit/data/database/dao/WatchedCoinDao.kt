package com.binarybricks.coinbit.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.binarybricks.coinbit.data.database.entities.WatchedCoin
import io.reactivex.Flowable
import io.reactivex.Single
import java.math.BigDecimal

/**
 * Created by Pragya Agrawal
 *
 * Add queries to read/update coinSymbol data from database.
 */
@Dao
interface WatchedCoinDao {

    @Query("select * from WatchedCoin where purchaseQuantity > 0 OR watched = :watched order by sortOrder")
    fun getAllWatchedCoins(watched: Boolean = true): Flowable<List<WatchedCoin>>

    @Query("select * from WatchedCoin where purchaseQuantity > 0 OR watched = :watched order by sortOrder")
    fun getAllWatchedCoinsOnetime(watched: Boolean = true): Single<List<WatchedCoin>> // this method should be removed

    @Query("select * from WatchedCoin where isTrading = :isTrue order by sortOrder")
    fun getAllCoins(isTrue: Boolean = true): Flowable<List<WatchedCoin>>

    @Query("select * from WatchedCoin where symbol = :symbol")
    fun getSingleWatchedCoin(symbol: String): Single<List<WatchedCoin>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCoinListIntoWatchList(list: List<WatchedCoin>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCoinIntoWatchList(watchedCoin: WatchedCoin)

    @Query("update WatchedCoin set purchaseQuantity = purchaseQuantity + :quantity where symbol=:symbol")
    fun addPurchaseQuantityForCoin(quantity: BigDecimal, symbol: String): Int

    @Query("UPDATE WatchedCoin SET watched = :watched  WHERE coinId = :coinId")
    fun makeCoinWatched(watched: Boolean, coinId: String)
}
