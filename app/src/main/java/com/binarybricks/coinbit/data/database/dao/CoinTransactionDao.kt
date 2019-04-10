package com.binarybricks.coinbit.data.database.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.binarybricks.coinbit.data.database.entities.CoinTransaction
import io.reactivex.Flowable

/**
 * Created by Pragya Agrawal
 *
 * Add queries to read/update coinSymbol transaction data from database.
 */
@Dao
interface CoinTransactionDao {

    @Query("select * from cointransaction")
    fun getAllCoinTransaction(): Flowable<List<CoinTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTransaction(coinTransaction: CoinTransaction)

    @Query("SELECT * FROM cointransaction WHERE coinSymbol = :coinSymbol ORDER BY transactionTime ASC")
    fun getTransactionsForCoin(coinSymbol: String): Flowable<List<CoinTransaction>>
}
