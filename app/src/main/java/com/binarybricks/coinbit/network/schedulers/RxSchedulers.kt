package com.binarybricks.coinbit.network.schedulers

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler

/**
 * Provides different types of schedulers.
 */
class RxSchedulers(val isTest: Boolean = false) {

    fun io(): Scheduler {
        return if (!isTest) Schedulers.io() else TestScheduler()
    }

    fun ui(): Scheduler {
        return if (!isTest) AndroidSchedulers.mainThread() else TestScheduler()
    }

    companion object {
        val instance: RxSchedulers by lazy {
            RxSchedulers()
        }
    }
}
