package com.binarybricks.coinbit.data.database.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

/**
 * Created by Pragya Agrawal
 */
@Entity(indices = [(Index("id", unique = true))])
@Parcelize
data class CoinTransaction(
    @ColumnInfo(name = "transactionType") var transactionType: Int,
    @ColumnInfo(name = "coinSymbol") var coinSymbol: String,
    @ColumnInfo(name = "pair") var pair: String,
    @ColumnInfo(name = "buyprice") var buyPrice: BigDecimal,
    @ColumnInfo(name = "buypriceHomeCurrency") var buypriceHomeCurrency: BigDecimal,
    @ColumnInfo(name = "quantity") var quantity: BigDecimal,
    @ColumnInfo(name = "transactionTime") var transactionTime: String,
    @ColumnInfo(name = "cost") var cost: String,
    @ColumnInfo(name = "exchange") var exchange: String,
    @ColumnInfo(name = "exchangeFees") var exchangeFees: BigDecimal,
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var idKey: Long = 0
) : Parcelable
