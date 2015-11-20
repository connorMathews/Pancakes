package me.mattlogan.pancakes

import android.os.Parcelable
import android.view.View
import android.view.ViewGroup

/**
 * Interface for deferred creation of View instances. A slice should be able to
 * persist its view state via a parcel.
 */
interface  Slice : Parcelable {
    fun toView(container: ViewGroup): View
    fun save(parcelable: Parcelable)
    fun restore(): Parcelable
}
