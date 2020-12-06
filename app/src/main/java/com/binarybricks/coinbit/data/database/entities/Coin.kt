package com.binarybricks.coinbit.data.database.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * Created by Pragya Agrawal
 */
@Entity(indices = [(Index("coinId", unique = true))])
@Parcelize
data class Coin(
    @ColumnInfo(name = "coinId") var id: String,
    @ColumnInfo(name = "url") var url: String,
    @ColumnInfo(name = "imageUrl") var imageUrl: String?,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "symbol") var symbol: String,
    @ColumnInfo(name = "coinName") var coinName: String,
    @ColumnInfo(name = "fullName") var fullName: String,
    @ColumnInfo(name = "algorithm") var algorithm: String?,
    @ColumnInfo(name = "proofType") var proofType: String?,
    @ColumnInfo(name = "fullyPremined") var fullyPremined: String?,
    @ColumnInfo(name = "totalCoinSupply") var totalCoinSupply: String?,
    @ColumnInfo(name = "preMinedValue") var preMinedValue: String?,
    @ColumnInfo(name = "totalCoinsFreeFloat") var totalCoinsFreeFloat: String?,
    @ColumnInfo(name = "sortOrder") var sortOrder: Int?,
    @ColumnInfo(name = "sponsored") var sponsored: Boolean = false,
    @ColumnInfo(name = "isTrading") var isTrading: Boolean = false,
    @ColumnInfo(name = "description") var description: String?,
    @ColumnInfo(name = "twitter") var twitter: String?,
    @ColumnInfo(name = "website") var website: String?,
    @ColumnInfo(name = "reddit") var reddit: String?,
    @ColumnInfo(name = "forum") var forum: String?,
    @ColumnInfo(name = "github") var github: String?,
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var idKey: Long = 0
) : Parcelable
