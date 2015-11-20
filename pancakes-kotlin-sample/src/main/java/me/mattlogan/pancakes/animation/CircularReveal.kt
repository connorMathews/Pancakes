package me.mattlogan.pancakes.animation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import me.mattlogan.pancakes.Spatula

object CircularReveal : Spatula {
    override fun flip(container: ViewGroup, from: View?,
                      to: View): Animator {
        // get the center for the clipping circle
        val cx = to.width / 2
        val cy = to.height / 2

        // get the final radius for the clipping circle
        val finalRadius = Math.max(to.width, to.height)

        // create the animator for this view (the start radius is zero)
        val animator = ViewAnimationUtils.createCircularReveal(to, cx, cy, 0f,
                finalRadius.toFloat()).setDuration(400)

        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                Log.d("testing", "Animation ending")

                // Remove red view when done.
                if (from != null) {
                    container.removeView(from)
                    animation.removeListener(this)
                    Log.d("testing", "Animation listener removed")
                }
            }
        })

        return animator
    }
}
