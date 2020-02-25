package com.glimpse.app.Helpers

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.tabs.TabLayout
import java.lang.reflect.Field


class CustomTabLayout : TabLayout {
    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context!!, attrs, defStyleAttr) {}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        try {
            if (tabCount == 0) return
            val field: Field = TabLayout::class.java.getDeclaredField("mScrollableTabMinWidth")
            field.isAccessible = true
            field.set(this, (measuredWidth / (tabCount).toFloat()).toInt())
            tabMode = MODE_SCROLLABLE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}