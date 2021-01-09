package com.binarybricks.coinbit.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.binarybricks.coinbit.data.database.entities.CoinTransaction
import kotlinx.coroutines.flow.Flow

/**
 * Created by Pragya Agrawal
 *
 * Add queries to read/update coinSymbol transaction data from database.
 */
@Dao
interface CoinTransactionDao {

    @Query("select * from cointransaction")
    fun getAllCoinTransaction(): Flow<List<CoinTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(coinTransaction: CoinTransaction)

    @Query("SELECT * FROM cointransaction WHERE coinSymbol = :coinSymbol ORDER BY transactionTime ASC")
    fun getTransactionsForCoin(coinSymbol: String): Flow<List<CoinTransaction>>
}
