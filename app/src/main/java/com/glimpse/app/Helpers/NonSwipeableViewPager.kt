package com.glimpse.app.Helpers

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import android.widget.Scroller
import androidx.viewpager.widget.ViewPager


class NonSwipeableViewPager : ViewPager{
    constructor(context: Context?) : super(context!!) {
        setMyScroller()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        setMyScroller()
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean { // Never allow swiping to switch between pages
        return false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean { // Never allow swiping to switch between pages
        return false
    }

//    override fun transformPage(page: View, position: Float) {
//        page.translationX = -position * page.width
//        if (Math.abs(position) < 0.5) {
//            page.visibility = View.VISIBLE
//            page.scaleX = 1 - Math.abs(position)
//            page.scaleY = 1 - Math.abs(position)
//        } else if (Math.abs(position) > 0.5) {
//            page.visibility = View.GONE
//        }
//    }

    //down one is added for smooth scrolling
    private fun setMyScroller() {
        try {
            val viewpager: Class<*> = ViewPager::class.java
            val scroller = viewpager.getDeclaredField("mScroller")
            scroller.isAccessible = true
            scroller[this] = MyScroller(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class MyScroller(context: Context?) : Scroller(context, DecelerateInterpolator()) {
        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
            super.startScroll(startX, startY, dx, dy, 350 /*1 secs*/)
        }
    }
}
