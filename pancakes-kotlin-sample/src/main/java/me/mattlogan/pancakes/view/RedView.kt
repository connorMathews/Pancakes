package me.mattlogan.pancakes.view

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RelativeLayout
import me.mattlogan.pancakes.Pancakes
import me.mattlogan.pancakes.PancakesActivity
import me.mattlogan.pancakes.R
import me.mattlogan.pancakes.Slice
import me.mattlogan.pancakes.animation.CircularReveal

class RedView(context: Context, attrs: AttributeSet) :
        RelativeLayout(context, attrs), Pancakes.Listener {

    class RedViewSlice(var redViewModelState: Parcelable) : Slice,
            Parcelable by redViewModelState {
        override fun toView(container: ViewGroup): View {
            return LayoutInflater.from(container.context)
                    .inflate(R.layout.view_red, container, false)
        }

        override fun save(parcelable: Parcelable) {
            redViewModelState = parcelable
        }

        override fun restore(): Parcelable {
            return redViewModelState
        }
    }

    lateinit var pancakes: Pancakes

    init {
        Log.d("testing", "RedView (" + hashCode() + ") created")
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        Log.d("testing", "RedView (" + hashCode() + ") onFinishInflate")

        pancakes = (context as PancakesActivity).pancakes()

        findViewById(R.id.red_button_back).setOnClickListener {
            Log.d("testing", "RedView popping itself")
            pancakes.pop()
        }

        findViewById(R.id.red_button_go_to_green).setOnClickListener {
            Log.d("testing", "RedView pushing GreenView")
            pancakes.push(GreenView.GreenViewSlice(
                    ColoredViewModel.builder().build()), CircularReveal)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.d("testing", "RedView (" + hashCode() + ") onAttachedToWindow")

        pancakes.addListener(this)

        // Restore!
        val viewModel = pancakes.peek().restore() as? ColoredViewModel
        if (viewModel is ColoredViewModel && viewModel.checkedId() != null) {
            (findViewById(R.id.red_radio_group) as RadioGroup)
                    .check(viewModel.checkedId())
        }
    }

    // Save callback.
    override fun onStackChange(event: Pancakes.StackChangeEvent) {
        if (event.kind.equals(Pancakes.StackChangeEvent.Kind.PUSH)) {
            val checked =
                    (findViewById(R.id.red_radio_group) as RadioGroup)
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
        Log.d("testing", "RedView (" + hashCode() + ") onDetachedFromWindow")

        pancakes.removeListener(this)
    }

    // Note: These instance state saving methods will only be called if the view has an id.
    override fun onSaveInstanceState(): Parcelable {
        Log.d("testing", "RedView (" + hashCode() + ") onSaveInstanceState")
        return super.onSaveInstanceState()
    }

    override fun onRestoreInstanceState(savedInstanceState: Parcelable) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d("testing", "RedView (" + hashCode() + ") onRestoreInstanceState")
    }
}
