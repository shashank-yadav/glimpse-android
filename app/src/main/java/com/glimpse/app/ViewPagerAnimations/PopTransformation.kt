package com.glimpse.app.ViewPagerAnimations

import android.view.View
import androidx.viewpager.widget.ViewPager

class PopTransformation : ViewPager.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.translationX = -position * page.width
        if (Math.abs(position) < 0.5) {
            page.visibility = View.VISIBLE
            page.scaleX = 1 - Math.abs(position)
            page.scaleY = 1 - Math.abs(position)
        } else if (Math.abs(position) > 0.5) {
            page.visibility = View.GONE
        }
    }
}
