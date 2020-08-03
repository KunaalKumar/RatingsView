package dev.kunaal.RatingsView

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.interpolator.view.animation.FastOutSlowInInterpolator


class RatingsView: View {

    private val ratingPercent = 80
    private var animatedPercent = 0
    private var currentNum = 360F

    private var arcPaint = Paint()
    private var arcColor: Int = ContextCompat.getColor(context, android.R.color.black)
    private var strokeWidth = 50F
    private val oval = RectF(0F, 0F, width.toFloat(), height.toFloat())
    private var animator: ValueAnimator = ValueAnimator.ofFloat(0F, 360 * ratingPercent * 0.01F)

    private var textBounds = Rect()
    private val textPaint = Paint()
    private var textColor: Int = ContextCompat.getColor(context, android.R.color.black)


    constructor (context: Context?) : this(context, null) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {

        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        val primaryColor = typedValue.data

        if(attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.RatingsView, 0, 0)
            arcColor = array.getColor(R.styleable.RatingsView_arcColor, primaryColor)
            textColor = array.getColor(R.styleable.RatingsView_textColor, primaryColor)
            array.recycle()
        }

        // Setup and start animation
            animator.duration = 1000
        animator.interpolator = FastOutSlowInInterpolator()
        animator.addUpdateListener { animator ->
            currentNum = animator.animatedValue as Float
            animatedPercent = (ratingPercent * animator.animatedFraction).toInt()
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
            color = arcColor
            strokeWidth = this@RatingsView.strokeWidth
            strokeCap = Paint.Cap.ROUND
            style = Paint.Style.STROKE
        }

        textPaint.apply {
            isAntiAlias = true
            color = textColor
            textSize = (width - paddingLeft - marginLeft - paddingRight - marginRight) * 0.35F
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            getTextBounds(ratingPercent.toString(), 0, ratingPercent.toString().length, textBounds)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        with(canvas) {
            drawArc(oval, 270f, -currentNum, false, arcPaint)
            drawText(animatedPercent.toString(), width / 2F, height / 2F + textBounds.height() / 2F, textPaint)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator.removeAllUpdateListeners()
    }
}