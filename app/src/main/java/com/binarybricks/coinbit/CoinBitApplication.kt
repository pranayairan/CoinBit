package com.binarybricks.coinbit

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import android.util.Log
import com.binarybricks.coinbit.data.database.CoinBitDatabase
import com.crashlytics.android.Crashlytics
import com.facebook.stetho.Stetho
import com.squareup.leakcanary.LeakCanary
import io.reactivex.plugins.RxJavaPlugins
import timber.log.Timber
import timber.log.Timber.DebugTree

/**
Created by Pranay Airan
 */

class CoinBitApplication : Application() {

    companion object {

        private const val DATABASE_NAME = "coinbit.db"

        private lateinit var appContext: Context
        var database: CoinBitDatabase? = null

        @JvmStatic
        fun getGlobalAppContext(): Context {
            return appContext
        }
    }

    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this)

        appContext = applicationContext

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
            Stetho.initializeWithDefaults(this)
        } else {
            Timber.plant(CrashReportingTree())
        }

        database = Room.databaseBuilder(this, CoinBitDatabase::class.java, DATABASE_NAME).build()

        // Logs all uncaught exceptions from RxJava usage and prevents default thread handling
        RxJavaPlugins.setErrorHandler { throwable -> Timber.e(throwable) }
    }

    /** A tree which logs important information for crash reporting.  */
    private class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return
            }
            if (priority == Log.ERROR) {
                Crashlytics.log(Log.ERROR, tag, message)
            } else if (priority == Log.WARN) {
                Crashlytics.log(Log.WARN, tag, message)
            }
        }
    }
}