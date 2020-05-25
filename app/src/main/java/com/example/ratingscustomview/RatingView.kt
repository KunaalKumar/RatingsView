package com.example.ratingscustomview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class RatingView: View {
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.setShadowLayer(12F, 0F, 0F, Color.RED)
        setLayerType(LAYER_TYPE_SOFTWARE, paint)

        canvas?.let {
            it.drawCircle(width / 2F, height / 2F, 100F, paint)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        val viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        setMeasuredDimension(
            Math.min(viewWidth, 250),
            Math.min(viewHeight, 250)
        )
    }
}