package me.mattlogan.pancakes.view

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import me.mattlogan.pancakes.PancakesActivity
import me.mattlogan.pancakes.R
import me.mattlogan.pancakes.Slice
import me.mattlogan.pancakes.animation.CircularHide

class BlueView(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs) {

    class BlueViewSlice(var blueViewModelState: Parcelable) : Slice,
            Parcelable by blueViewModelState {
        override fun toView(container: ViewGroup): View {
            return LayoutInflater.from(container.context)
                    .inflate(R.layout.view_blue, container, false)
        }

        override fun save(parcelable: Parcelable) {
            blueViewModelState = parcelable
        }

        override fun restore(): Parcelable {
            return blueViewModelState
        }
    }

    init {
        Log.d("testing", "BlueView (" + hashCode() + ") created")
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        Log.d("testing", "BlueView (" + hashCode() + ") onFinishInflate")

        val pancakes = (context as PancakesActivity).pancakes()

        findViewById(R.id.blue_button_back).setOnClickListener {
            Log.d("testing", "BlueView popping itself")
            pancakes.pop(CircularHide)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.d("testing", "BlueView (" + hashCode() + ") onAttachedToWindow")
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Log.d("testing", "BlueView (" + hashCode() + ") onDetachedFromWindow")
    }

    // Note: These instance state saving methods will only be called if the view has an id.
    override fun onSaveInstanceState(): Parcelable {
        Log.d("testing", "BlueView (" + hashCode() + ") onSaveInstanceState")
        return super.onSaveInstanceState()
    }

    override fun onRestoreInstanceState(savedInstanceState: Parcelable) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d("testing", "BlueView (" + hashCode() + ") onRestoreInstanceState")
    }
}
