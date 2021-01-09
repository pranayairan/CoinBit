package com.binarybricks.coinbit.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.binarybricks.coinbit.data.database.entities.WatchedCoin
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

/**
 * Created by Pragya Agrawal
 *
 * Add queries to read/update coinSymbol data from database.
 */
@Dao
interface WatchedCoinDao {

    @Query("select * from WatchedCoin where purchaseQuantity > 0 OR watched = :watched order by sortOrder")
    fun getAllWatchedCoins(watched: Boolean = true): Flow<List<WatchedCoin>>

    @Query("select * from WatchedCoin where purchaseQuantity > 0 OR watched = :watched order by sortOrder")
    suspend fun getAllWatchedCoinsOnetime(watched: Boolean = true): List<WatchedCoin> // this method should be removed

    @Query("select * from WatchedCoin where isTrading = :isTrue order by sortOrder")
    fun getAllCoins(isTrue: Boolean = true): Flow<List<WatchedCoin>>

    @Query("select * from WatchedCoin where symbol = :symbol")
    suspend fun getSingleWatchedCoin(symbol: String): List<WatchedCoin>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoinListIntoWatchList(list: List<WatchedCoin>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoinIntoWatchList(watchedCoin: WatchedCoin)

    @Query("update WatchedCoin set purchaseQuantity = purchaseQuantity + :quantity where symbol=:symbol")
    suspend fun addPurchaseQuantityForCoin(quantity: BigDecimal, symbol: String): Int

    @Query("UPDATE WatchedCoin SET watched = :watched  WHERE coinId = :coinId")
    suspend fun makeCoinWatched(watched: Boolean, coinId: String)
}
