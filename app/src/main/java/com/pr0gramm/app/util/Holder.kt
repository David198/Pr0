package com.pr0gramm.app.util

import com.google.common.util.concurrent.SettableFuture
import rx.Observable
import rx.Single
import rx.subjects.BehaviorSubject

/**
 * Holds an [rx.Single] value and provides access
 * to the result.
 */
class Holder<T> private constructor(single: Single<T>) {
    private val subject = BehaviorSubject.create<T>()
    private val future = SettableFuture.create<T>()

    init {
        single.subscribe(
                { value ->
                    future.set(value)
                    subject.onNext(value)
                },
                { error ->
                    future.setException(error)
                    subject.onError(error)
                })
    }

    fun asObservable(): Observable<T> {
        return subject.take(1)
    }

    /**
     * Gets the value of this holder. This will block, if the value
     * is not yet present.
     */
    val value: T get() = future.get()

    /**
     * Returns the current value or returns null, if the value is
     * not yet available.
     */
    val valueOrNull: T? get() {
        if (future.isDone) {
            try {
                return future.get()
            } catch(ignored: Exception) {
            }
        }

        return null
    }

    companion object {
        @JvmStatic
        fun <T> ofSingle(single: Single<T>): Holder<T> {
            return Holder(single)
        }

        @JvmStatic
        fun <T> ofObservable(observable: Observable<T>): Holder<T> {
            return ofSingle(observable.toSingle())
        }
    }
}
