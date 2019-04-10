package com.binarybricks.coinbit.data.database.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.binarybricks.coinbit.data.database.entities.Exchange
import io.reactivex.Single

/**
 * Created by Pragya Agrawal
 *
 * Add queries to read/update exchange data from database.
 */
@Dao
interface ExchangeDao {

    @Query("select * from exchange")
    fun getAllExchanges(): Single<List<Exchange>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExchanges(list: List<Exchange>)
}
