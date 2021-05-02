package com.example.triptracker.helpers.Utils

import android.content.Context
import android.graphics.*
import android.util.Log
import com.example.triptracker.R
import com.google.android.gms.maps.model.LatLng
import kotlin.math.abs
import kotlin.math.atan


object MapUtils {

    private const val TAG = "MapUtils"

    fun getStopBitmap(): Bitmap {
        val height = 20
        val width = 20
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), paint)
        return bitmap
    }

}