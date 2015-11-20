package me.mattlogan.pancakes

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import java.util.*

/**
 * Manages a navigation stack by representing each item in the stack as a
 * Slice, which is responsible for View creation, last View state.
 *
 * Slices can come with a Spatula to handle transition animations. Spatula
 * animations are run sequentially and kept in a queue. Pancakes' state won't
 * change during an ongoing animation.
 *
 * All standard Java Stack operations are supported and Pancakes is backed by
 * an ArrayList that can be persisted to a Bundle. Slices must implement or
 * delegate Parcelable to take advantage.
 *
 * @param [delegate] Callback block for "finishing" a navigation stack. The
 *                   block will be followed by a call to finish() in the host
 *                   Activity.
 */
class Pancakes(
        val container: ViewGroup,
        val delegate: (Activity.() -> Unit)?) : AnimatorListenerAdapter() {

    val activity: Activity? = container.context as? Activity
    var backing: ArrayList<Slice> = ArrayList()
    var listeners: MutableList<Listener> = ArrayList()
    val ongoing: MutableList<Animator> = ArrayList()

    /**
     * Resets the navigation stack state to what it was when saveToBundle() was
     * called.
     *
     * @param [savedInstanceState] A [Bundle] containing Pancakes' state
     */
    fun onLoad(savedInstanceState: Bundle) {
        backing = savedInstanceState.getParcelableArrayList<Slice>(
                "PancakeStack");
        container.addView(peek().toView(container))
    }

    /**
     * Saves Pancakes' state to the provided Bundle.
     *
     * @param [outState] The [Bundle] in which to save the serialized
     *                   [ArrayList] of [Slice]'s.
     */
    fun onSave(outState: Bundle) {
        outState.putParcelableArrayList("PancakeStack", backing)
    }

    /**
     * Pushes a Slice onto the navigation stack with an optional Spatula
     * (transition animation).
     *
     * @param [slice] responsible for the creation of the next View in the
     *                navigation stack
     * @param [spatula]
     * @return the provided [Slice] (to comply with the Java Stack API)
     */
    fun push(slice: Slice, spatula: Spatula = Spatula.NONE): Slice? {

        fun <V> ArrayList<V>.push(t: V): V {
            add(t)
            return t
        }

        if (!ongoing.isEmpty()) {
            return null
        }

        val top = slice.toView(container)
        val below = if (size() > 0) peekView() else null

        notifyListeners(StackChangeEvent.Kind.PUSH, slice)
        backing.push(slice)
        container.addView(top)

        top.viewTreeObserver.addOnGlobalLayoutListener(
                // Cannot be lambda b/c we need self reference to remove the
                // listener after layout.
                object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        top.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        val animator = spatula.flip(container, below, top)
                        animator.addListener(this@Pancakes)
                        animator.startOrWait()
                    }
                })

        return slice
    }

    internal fun Animator.startOrWait() {
        if (ongoing.isEmpty()) {
            ongoing.add(this)
            start()
        } else {
            ongoing.add(this)
        }
    }

    override fun onAnimationEnd(animation: Animator) {
        ongoing.remove(animation)
        if (!ongoing.isEmpty()) {
            ongoing.peek().start()
        }
    }

    internal fun <T> MutableList<T>.peek(): T {
        return this[size - 1]
    }

    /**
     * Pops the top View off the navigation stack.
     *
     * @return the ViewFactory instance that was used for the creation of the
     * top View on the navigation stack. Will return null if popping the last
     * view in the stack.
     */
    fun pop(spatula: Spatula = Spatula.NONE): Slice? {
        if (backing.isEmpty()) {
            throw EmptyStackException()
        }

        if (!ongoing.isEmpty()) {
            return null
        }

        if (size() == 1) {
            activity?.finishStack(notify = true)
            return null
        }

        fun <T> MutableList<T>.pop(): T {
            val popped = this[size - 1]
            this.removeAt(size - 1)
            return popped
        }

        val top = peekView()
        val popped = backing.pop()
        notifyListeners(StackChangeEvent.Kind.POP, popped)

        val hasBottom = sizeView() > 1
        val bottom = if (hasBottom) peekView() else peek().toView(container)

        val animator = spatula.flip(container, top, bottom)

        if (hasBottom) {
            animator.start()
        } else {
            // Add below current view.
            container.addView(bottom)
            top.bringToFront()
            bottom.viewTreeObserver.addOnGlobalLayoutListener(
                    // Cannot be lambda b/c we need self reference to remove the
                    // listener after layout.
                    object : ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            bottom.viewTreeObserver
                                    .removeOnGlobalLayoutListener(this)
                            animator.addListener(this@Pancakes)
                            animator.startOrWait()
                        }
                    })
        }

        return popped
    }

    /**
     * @return the [Slice] responsible for creating the top View on the
     * navigation stack.
     */
    fun peek(): Slice {
        if (backing.isEmpty()) {
            throw EmptyStackException()
        }

        return backing.peek()
    }

    /**
     * @return the View child at the top of the navigation stack.
     */
    internal fun peekView(): View {
        if (sizeView() == 0) {
            throw EmptyStackException()
        }
        return container.getChildAt(container.childCount - 1)
    }

    /**
     * @return the size of the view container.
     */
    internal fun sizeView(): Int {
        return container.childCount
    }

    /**
     * @return the size of the navigation stack
     */
    fun size(): Int {
        return backing.size
    }

    /**
     * Clears the navigation stack and removes all Views from the provided
     * ViewGroup container. Will stop ongoing animations and clear the queue.
     */
    fun clear(notify: Boolean = false) {
        if (notify) notifyListeners(StackChangeEvent.Kind.FINISH)
        ongoing.peek().end()
        ongoing.clear()
        backing.clear()
        container.removeAllViews()
    }

    fun Activity.finishStack(notify: Boolean = false) {
        if (notify) notifyListeners(StackChangeEvent.Kind.FINISH)
        delegate?.invoke(this)
        finish()
    }

    /**
     * Listener for stack change events.
     */
    interface Listener {
        /**
         * Called when BOTH the ViewStack's size AND the top View in the
         * ViewGroup container have changed. For a push with an animation, this
         * happens before the animation starts (right after the new View is
         * added to the container). For a pop with an animation, this happens
         * after the animation completes (right after the old view is removed
         * from the container).
         */
        fun onStackChange(event: StackChangeEvent)
    }

    /**
     * An event for stack changes.
     */
    data class StackChangeEvent(
            val kind: StackChangeEvent.Kind,
            val slice: Slice?) {
        enum class Kind {
            // Push events come before the push has occurred with the new slice.
            PUSH,
            // Pop notifications come after the pop has occurred with the popped
            // slice.
            POP,
            CLEAR,
            FINISH
        }
    }

    /**
     * Adds a [Listener] for stack change events.
     *
     * @param listener A StackChangedListener.
     */
    fun addListener(listener: Listener) {
        listeners.add(listener)
    }

    /**
     * Removes the supplied [Listener] from stack change events.
     *
     * @param listener The [Listener] to remove.
     * @return true if the [Listener] was actually removed.
     */
    fun removeListener(listener: Listener): Boolean {
        return listeners.remove(listener)
    }

    /**
     * Dispatch stack change events to subscribed [Listener]'s.
     */
    internal fun notifyListeners(changeType: StackChangeEvent.Kind,
                                 slice: Slice? = null) {
        listeners.forEach {
            it.onStackChange(StackChangeEvent(changeType, slice))
        }
    }

    /**
     * Clear out the stack, but let our listeners know what's coming.
     */
    internal fun clearListeners() {
        notifyListeners(StackChangeEvent.Kind.CLEAR)
        listeners.clear()
    }
}
