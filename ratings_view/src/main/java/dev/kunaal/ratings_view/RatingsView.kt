package dev.kunaal.ratings_view

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
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
import java.util.*
import kotlin.collections.set
import kotlin.math.min

/**
 * Ratings view
 *
 * @constructor
 *
 * @param context
 * @param attrs
 * @param defStyleAttr
 */
class RatingsView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : View(context, attrs, defStyleAttr) {

    /**
     * Rating number to display.
     *
     * Arc percentage is based off of this value.
     *
     * Setting the value will automatically update view.
     */
    var rating = 0
        set(value) {
            field = value
            startAnimation()
        }

    private var animatedRating = 0
    private var currentNum = 0F

    private var lastColor = 0
    private lateinit var arcAnimator: ValueAnimator
    private lateinit var numberAnimator: ValueAnimator
    private lateinit var colorAnimator: ValueAnimator
    private var animatorSet = AnimatorSet()

    private var arcPaint = Paint()
    private var arcColor: Int = ContextCompat.getColor(context, android.R.color.black)
    private var strokeWidth = 50F
    private val oval = RectF(0F, 0F, width.toFloat(), height.toFloat())

    private var bgPaint = Paint()
    private var bgColor: Int = ContextCompat.getColor(context, android.R.color.black)

    private var textBounds = Rect()
    private val textPaint = Paint()
    private var textColor: Int = ContextCompat.getColor(context, android.R.color.black)

    // Color ranges map indicating the color to be displayed at percent
    private val colorRangeMap = TreeMap<Int, Int>()

    /**
     * Adds the arc color to be displayed within the given threshold.
     * To change color, simply override the threshold
     *
     * Example: Calling `addThreshold(10, Color.RED)`, will display the color red for values 0-10
     * Furthermore, calling `addThreshold(5, Color.BLACK)`, will display the color black from 0-5
     *   and the color red from 6-10
     *
     * @param threshold number below which color should be displayed
     * @param color color to be displayed within the threshold limits
     */
    fun addArcThresholdColor(threshold: Int, color: Int) {
        if (threshold != 0 && !colorRangeMap.containsKey(0))
            colorRangeMap[0] = arcColor
        colorRangeMap[threshold] = color
        startColorAnimation(colorRangeMap.floorEntry(rating)!!.value)
        postInvalidate()
    }

    /**
     * Adds given map of arc color to be displayed within the given threshold.
     * To change color, simply override the threshold
     *
     * @param map map of threshold(key) and color(value) to be displayed within the threshold limits
     */
    fun addArcThresholdColor(map: Map<Int, Int>) {
        if (!map.containsKey(0))
            colorRangeMap[0] = arcColor
        colorRangeMap.putAll(map)
        startColorAnimation(colorRangeMap.floorEntry(rating)!!.value)
        postInvalidate()
    }

    /**
     * Removes the arc color to be displayed for the threshold
     * NOTE: Threshold value of `0` must be the last one to be deleted,
     *   ie. all other values must be deleted first
     *
     * @param threshold
     */
    fun removeArcThresholdColor(threshold: Int) {
        if (threshold == 0 && colorRangeMap.size > 1)
            throw RuntimeException("Threshold of value '0' must be the last to be removed")

        colorRangeMap.remove(threshold)
        if (colorRangeMap.isEmpty())
            startColorAnimation(arcColor)
        else
            startColorAnimation(colorRangeMap.floorEntry(rating)!!.value)
        postInvalidate()
    }

    /**
     * Removes all threshold colors for the arc
     *
     */
    fun removeAllArcThresholdColor() {
        colorRangeMap.clear()
        startColorAnimation(arcColor)
        postInvalidate()
    }

    init {
        val primValue = TypedValue()
        context.theme.resolveAttribute(R.attr.colorPrimary, primValue, true)
        val primaryColor = primValue.data

        val bgValue = TypedValue()
        context.theme.resolveAttribute(R.attr.background, primValue, true)
        val defaultBgColor = bgValue.data

        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.RatingsView, 0, 0)
            arcColor = array.getColor(R.styleable.RatingsView_arcColor, primaryColor)
            bgColor = array.getColor(R.styleable.RatingsView_bgColor, defaultBgColor)
            textColor = array.getColor(R.styleable.RatingsView_textColor, primaryColor)
            array.recycle()
        }

        startAnimation()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        strokeWidth = width * 0.10F

        oval.set(
                paddingLeft + marginLeft.toFloat(),
                paddingTop + marginTop.toFloat(),
                measuredWidth.toFloat() - paddingRight - marginRight,
                measuredHeight.toFloat() - paddingBottom - marginBottom
        )

        arcPaint.apply {
            isAntiAlias = true
            color = arcColor
            strokeWidth = this@RatingsView.strokeWidth
            strokeCap = Paint.Cap.ROUND
            style = Paint.Style.STROKE
        }

        bgPaint.apply {
            isAntiAlias = true
            color = bgColor
        }

        textPaint.apply {
            isAntiAlias = true
            color = textColor
            textSize = (measuredWidth - paddingLeft - marginLeft - paddingRight - marginRight) * 0.35F
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            getTextBounds(rating.toString(), 0, rating.toString().length, textBounds)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        with(canvas) {
            drawArc(oval, 0F, 360F, true, bgPaint)
            drawArc(oval, 270f, -currentNum, false, arcPaint)
            drawText(animatedRating.toString(), width / 2F, measuredHeight / 2F + textBounds.height() / 2F, textPaint)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val size = min(widthSize, heightSize)
        setPadding(60, 60, 60, 60)
        setMeasuredDimension(size, size)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        arcAnimator.removeAllUpdateListeners()
        numberAnimator.removeAllUpdateListeners()
    }

    /**
     * Starts arc and number animation.
     * Number animation also calls startColorAnimation() if required
     */
    private fun startAnimation() {
        if (animatorSet.isRunning)
            animatorSet.cancel()

        arcAnimator = ValueAnimator.ofFloat(currentNum, 360 * rating * 0.01F)
        arcAnimator.addUpdateListener { animator ->
            currentNum = animator.animatedValue as Float
            postInvalidate()
        }

        numberAnimator = ValueAnimator.ofInt(animatedRating, rating).apply {
            interpolator = FastOutSlowInInterpolator()
            addUpdateListener {
                animatedRating = animatedValue as Int
                if (colorRangeMap.isNotEmpty() && colorRangeMap.floorKey(animatedRating)!! != lastColor) {
                    lastColor = colorRangeMap.floorKey(animatedRating)
                    startColorAnimation(colorRangeMap[lastColor]!!)
                }
                postInvalidate()
            }
        }

        animatorSet = AnimatorSet().apply {
            duration = 1000
            interpolator = FastOutSlowInInterpolator()
            playTogether(arcAnimator, numberAnimator)
            start()
        }
    }

    /**
     * Starts animating arc color to given color
     *
     * @param toColor color to animate to
     */
    private fun startColorAnimation(toColor: Int) {
        if (::colorAnimator.isInitialized && colorAnimator.isRunning)
            colorAnimator.cancel()

        colorAnimator = ValueAnimator().apply {
            setIntValues(arcPaint.color, toColor)
            setEvaluator(ArgbEvaluator())
            addUpdateListener {
                arcPaint.color = animatedValue as Int
                postInvalidate()
            }
            start()
        }
    }
}
