package com.binarybricks.coinbit.features

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.CompositeDisposable

/**
 * A base class for all our presenters. It provides the basics nuts & bolts of attaching/detaching a presenter to/from a
 * view, as well as the strings resolution class.
 */

open class BasePresenter<V : BaseView> : LifecycleObserver {

    protected var currentView: V? = null

    /**
     * Composite disposable for dispose all the disposable. The concrete implementation of this class should add all the
     * disposables to this and BasePresenter will take care of clearing it up when exit the view.
     */
    protected val compositeDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    /**
     * Check if the view is attached.
     * This checking is only necessary when returning from an asynchronous call
     *
     * @return true if a view is attached to this presenter. false otherwise.
     */
    protected val isViewAttached: Boolean get() = currentView != null

    fun attachView(view: V) {

        if (currentView != null) {
            currentView = null
        }
        currentView = view
    }

    fun detachView() {
        compositeDisposable.dispose()
        currentView = null
    }

    // cleanup
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun cleanYourSelf() {
        detachView()
    }
}
