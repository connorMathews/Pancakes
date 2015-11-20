package me.mattlogan.pancakes.view

import android.content.Context
import android.os.Parcelable
import android.support.annotation.Nullable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.RelativeLayout
import auto.parcel.AutoParcel
import me.mattlogan.pancakes.Pancakes
import me.mattlogan.pancakes.PancakesActivity
import me.mattlogan.pancakes.R
import me.mattlogan.pancakes.Slice
import me.mattlogan.pancakes.animation.CircularHide
import me.mattlogan.pancakes.animation.CircularReveal

class GreenView(context: Context, attrs: AttributeSet) :
        RelativeLayout(context, attrs), Pancakes.Listener {

    class GreenViewSlice(var greenViewModelState: Parcelable) : Slice,
            Parcelable by greenViewModelState {
        override fun toView(container: ViewGroup): View {
            return LayoutInflater.from(container.context)
                    .inflate(R.layout.view_green, container, false)
        }

        override fun save(parcelable: Parcelable) {
            greenViewModelState = parcelable
        }

        override fun restore(): Parcelable {
            return greenViewModelState
        }
    }

    lateinit var pancakes: Pancakes

    init {
        Log.d("testing", "GreenView (" + hashCode() + ") created")
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        Log.d("testing", "GreenView (" + hashCode() + ") onFinishInflate")

        pancakes = (context as PancakesActivity).pancakes()

        findViewById(R.id.green_button_back).setOnClickListener {
            Log.d("testing", "GreenView popping itself")
            pancakes.pop(CircularHide)
        }

        findViewById(R.id.green_button_go_to_blue).setOnClickListener {
            Log.d("testing", "GreenView pushing BlueView")
            pancakes.push(BlueView.BlueViewSlice(
                    ColoredViewModel.builder().build()), CircularReveal)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.d("testing", "GreenView (" + hashCode() + ") onAttachedToWindow")

        // Save callback!
        pancakes.addListener(this)

        // Restore!
        val viewModel = pancakes.peek().restore() as? ColoredViewModel
        if (viewModel is ColoredViewModel && viewModel.checkedId() != null) {
            (findViewById(R.id.green_radio_group) as RadioGroup)
                    .check(viewModel.checkedId())
        }
    }

    override fun onStackChange(event: Pancakes.StackChangeEvent) {
        if (event.kind.equals(Pancakes.StackChangeEvent.Kind.PUSH)) {
            val checked =
                    (findViewById(R.id.green_radio_group) as RadioGroup)
                            .checkedRadioButtonId

            pancakes.peek().save(
                    ColoredViewModel.builder()
                            .checkedId(checked)
                            .build())
        }

        pancakes.removeListener(this)
    }

    // Note: This won't be called when we push the next View onto the stack because this View is
    // kept in the container's view hierarchy. It's visibility is just set to gone.
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Log.d("testing", "GreenView (" + hashCode() + ") onDetachedFromWindow")

        pancakes.removeListener(this)
    }

    // Note: These instance state saving methods will only be called if the view has an id.
    override fun onSaveInstanceState(): Parcelable {
        Log.d("testing", "GreenView (" + hashCode() + ") onSaveInstanceState")
        return super.onSaveInstanceState()
    }

    override fun onRestoreInstanceState(savedInstanceState: Parcelable) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d("testing", "GreenView (" + hashCode() + ") onRestoreInstanceState")
    }
}
