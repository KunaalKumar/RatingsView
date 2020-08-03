package com.example.ratingscustomview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop

class RatingView: View {

    private var currentNum = 1F
    private var endNum = 83

    private var arcPaint = Paint()
    private var strokeWidth = 50F
    private val oval = RectF(0F, 0F, width.toFloat(), height.toFloat())
    private var animator: ValueAnimator = ValueAnimator.ofFloat(0F, 360 * endNum * 0.01F)

    private var textBounds = Rect()
    private var text: String = "100"
    private val textPaint = Paint()


    constructor (context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    init {
        animator.duration = 1000
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener { animator ->
            currentNum = animator.animatedValue as Float
            postInvalidate()
        }
        animator.start()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        strokeWidth = width * 0.05F

        oval.set(paddingLeft + marginLeft.toFloat(),
            paddingTop + marginTop.toFloat(),
            width.toFloat() - paddingRight - marginRight,
            height.toFloat() - paddingBottom - marginBottom)

        arcPaint.apply {
            isAntiAlias = true
            color = Color.BLACK
            strokeWidth = this@RatingView.strokeWidth
            strokeCap = Paint.Cap.ROUND
            style = Paint.Style.STROKE
        }

        textPaint.apply {
            isAntiAlias = true
            color = Color.BLACK
            textSize = (width - paddingLeft - marginLeft - paddingRight - marginRight) * 0.35F
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            getTextBounds(text, 0, text.length, textBounds)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        with(canvas) {
            drawArc(oval, 270f, -currentNum, false, arcPaint)
            drawText(text, width / 2F, height / 2F + textBounds.height() / 2F, textPaint)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator.removeAllUpdateListeners()
    }
}