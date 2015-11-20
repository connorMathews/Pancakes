package me.mattlogan.pancakes.animation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import me.mattlogan.pancakes.Spatula

object CircularHide : Spatula {
    override fun flip(container: ViewGroup, from: View?,
                      to: View): Animator {
        // get the center for the clipping circle
        val cx = from!!.width / 2
        val cy = from.height / 2

        // get the initial radius for the clipping circle
        val initialRadius = from.width

        // create the animation (the final radius is zero)
        val animator = ViewAnimationUtils.createCircularReveal(from, cx, cy,
                initialRadius.toFloat(), 0f).setDuration(400)

        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                Log.d("testing", "Animation ending")
                container.removeView(from)
                animator.removeListener(this)
                Log.d("testing", "Animation listener removed")
            }
        })

        return animator
    }
}
