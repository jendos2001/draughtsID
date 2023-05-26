package com.example.draughtsid

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View


class DraughtsView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var originX = 20f
    private var originY = 200f
    private var cellSide = 130f
    private var paint = Paint()
    private var position = ""
    private var blackCell = Color.rgb(240,224,183)
    private var whiteCell = Color.rgb(186, 139, 99)

    override fun onDraw(canvas: Canvas?) {
        Log.d("CCCCC", position)
        for (j in 0..7)
            for (i in 0..7){
                paint.color = if ((i + j) % 2 == 0) blackCell else whiteCell
                canvas?.drawRect(originX + i * cellSide, originY + j * cellSide, originX + (i + 1) * cellSide, originY + (j + 1) * cellSide, paint)
            }
        if (position != ""){
            drawPieces(canvas, position)
        }
    }

    fun drawPieces(canvas: Canvas?, string: String){
        var piece: String
        var field: String
        var let = 0
        var num: Int
        val tmp = string.split(":")
        val white = tmp[1].split(",")
        val black = tmp[2].split(",")
        for (i in white.indices){
            piece = if (i == 0) white[i].substring(1, white[i].length) else white[i]
            field = if (piece.length == 3) piece.substring(1, piece.length) else piece
            Log.d("FIG", field)
            when (field[0]) {
                'a' -> let = 0
                'b' -> let = 1
                'c' -> let = 2
                'd' -> let = 3
                'e' -> let = 4
                'f' -> let = 5
                'g' -> let = 6
                'h' -> let = 7

            }
            num = field[1].digitToInt()
            if (piece[0] == 'K'){
                val drawable = resources.getDrawable(R.drawable.white_king)
                val bitmap = Bitmap.createBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )
                canvas?.drawBitmap(bitmap, null,
                    RectF(
                        originX + let * cellSide,
                        originY + (7 - num) * cellSide,
                        originX + (let + 1) * cellSide,
                        originY + (7 - num + 1) * cellSide
                    ), paint)
            }
            else {
                val drawable = resources.getDrawable(R.drawable.white_draught)
                val bitmap = Bitmap.createBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )
                canvas?.drawBitmap(bitmap, null,
                    RectF(
                        originX + let * cellSide,
                        originY + (7 - num) * cellSide,
                        originX + (let + 1) * cellSide,
                        originY + (7 - num + 1) * cellSide
                    ), paint)
            }
        }
        for (i in black.indices){
            piece = if (i == 0) black[i].substring(1, black[i].length) else black[i]
            field = if (piece.length == 3) piece.substring(1, piece.length) else piece
            when (field[0]) {
                'a' -> let = 0
                'b' -> let = 1
                'c' -> let = 2
                'd' -> let = 3
                'e' -> let = 4
                'f' -> let = 5
                'g' -> let = 6
                'h' -> let = 7

            }
            num = field[1].digitToInt()
            if (piece[0] == 'K'){
                val drawable = resources.getDrawable(R.drawable.black_king)
                val bitmap = Bitmap.createBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )
                canvas?.drawBitmap(bitmap, null,
                    RectF(
                        originX + let * cellSide,
                        originY + (7 - num) * cellSide,
                        originX + (let + 1) * cellSide,
                        originY + (7 - num + 1) * cellSide
                    ), paint)
            }
            else {
                val drawable = resources.getDrawable(R.drawable.black_draught)
                val bitmap = Bitmap.createBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )
                canvas?.drawBitmap(bitmap, null,
                    RectF(
                        originX + let * cellSide,
                        originY + (7 - num) * cellSide,
                        originX + (let + 1) * cellSide,
                        originY + (7 - num + 1) * cellSide
                    ), paint)
            }
        }
    }

    fun setPosition(string: String){
        position = string
        invalidate()
    }
}