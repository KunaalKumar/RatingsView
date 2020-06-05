package com.example.ratingscustomview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator

class RatingView: View {
    constructor (context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    var currentNum = 0.8f

    private var animator: ValueAnimator = ValueAnimator.ofFloat(0f, 0.8f)

    init {
        animator.duration = 1000
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener { animator ->
            currentNum = animator.animatedValue as Float
            invalidate()
        }
        animator.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val paint = Paint().apply {
            isAntiAlias = true
            color = Color.BLACK
            strokeWidth = 100F
            strokeCap = Paint.Cap.ROUND
            style = Paint.Style.STROKE
        }

        val oval = RectF(100f, 100f, 500f, 500f)

        canvas.let {
            it.drawArc(oval, 270f, currentNum * 360f, false, paint)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator.removeAllUpdateListeners()
    }
}