package com.binarybricks.coinbit.data.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.binarybricks.coinbit.data.database.dao.CoinTransactionDao
import com.binarybricks.coinbit.data.database.dao.ExchangeDao
import com.binarybricks.coinbit.data.database.dao.WatchedCoinDao
import com.binarybricks.coinbit.data.database.entities.CoinTransaction
import com.binarybricks.coinbit.data.database.entities.Exchange
import com.binarybricks.coinbit.data.database.entities.WatchedCoin

/**
 * Created by Pragya Agrawal
 */
@Database(entities = [Exchange::class, WatchedCoin::class, CoinTransaction::class], version = 1, exportSchema = false)
@TypeConverters(BigDecimalConverter::class)
abstract class CoinBitDatabase : RoomDatabase() {

    abstract fun exchangeDao(): ExchangeDao
    abstract fun watchedCoinDao(): WatchedCoinDao
    abstract fun coinTransactionDao(): CoinTransactionDao
}
