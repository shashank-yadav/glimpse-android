package com.example.homemaker.Helpers

import android.graphics.Bitmap
import android.view.View


class ImageSaver{
    fun loadBitmapFromView(v: View): Bitmap? {
        val bitmap: Bitmap
        v.setDrawingCacheEnabled(true)
        bitmap = Bitmap.createBitmap(v.getDrawingCache())
        v.setDrawingCacheEnabled(false)
        return bitmap
    }
}