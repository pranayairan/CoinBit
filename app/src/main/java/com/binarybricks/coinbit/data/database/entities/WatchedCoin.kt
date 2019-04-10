package com.binarybricks.coinbit.data.database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

/**
 * Created by Pragya Agrawal
 */
@Entity(indices = [(Index("watched_id", unique = true))])
@Parcelize
data class WatchedCoin(
    @Embedded
    val coin: Coin,
    var exchange: String,
    var fromCurrency: String,
    var purchaseQuantity: BigDecimal = BigDecimal.ZERO,
    var watched: Boolean = false,
    @ColumnInfo(name = "watched_id") @PrimaryKey(autoGenerate = true) var idKey: Long = 0
) : Parcelable
