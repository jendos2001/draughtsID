package com.example.draughtsid

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class DraughtsView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var originX = 20f
    private var originY = 200f
    private var cellSide = 130f

    override fun onDraw(canvas: Canvas?) {
        val paint = Paint()
        for (j in 0..7)
            for (i in 0..7){
                paint.color = if ((i + j) % 2 == 0) Color.LTGRAY else Color.DKGRAY
                canvas?.drawRect(originX + i * cellSide, originY + j * cellSide, originX + (i + 1) * cellSide, originY + (j + 1) * cellSide, paint)
            }
    }

}