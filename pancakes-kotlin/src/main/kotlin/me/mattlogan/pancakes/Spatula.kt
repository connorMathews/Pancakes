package me.mattlogan.pancakes

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TimeInterpolator
import android.view.View
import android.view.ViewGroup

/**
 * Interface for creating [Animator] instances for push() and pop() transitions.
 *
 * Spatulas should be implemented as singletons or provide an overridden equals
 * so that they can be de-duped.
 *
 * Note: Spatula is responsible for removing a view from the container if you
 * want to completely replace it.
 */
interface Spatula {
    fun flip(container: ViewGroup, from: View?, to: View): Animator

    companion object {
        val NONE: Spatula = object : Spatula {
            override fun flip(container: ViewGroup, from: View?,
                              to: View): Animator {
                return object : Animator() {
                    override fun setInterpolator(value: TimeInterpolator?) {
                    }

                    override fun isRunning(): Boolean {
                        return false
                    }

                    override fun getDuration(): Long {
                        return 0
                    }

                    override fun getStartDelay(): Long {
                        return 0
                    }

                    override fun setStartDelay(startDelay: Long) {
                    }

                    override fun setDuration(duration: Long): Animator? {
                        return this
                    }

                    override fun start() {
                        listeners.forEach { it.onAnimationEnd(this) }
                    }
                }
            }
        }
    }
}
