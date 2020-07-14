package com.binarybricks.coinbit.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.binarybricks.coinbit.data.database.entities.Exchange

/**
 * Created by Pragya Agrawal
 *
 * Add queries to read/update exchange data from database.
 */
@Dao
interface ExchangeDao {

    @Query("select * from exchange")
    suspend fun getAllExchanges(): List<Exchange>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExchanges(list: List<Exchange>)
}
