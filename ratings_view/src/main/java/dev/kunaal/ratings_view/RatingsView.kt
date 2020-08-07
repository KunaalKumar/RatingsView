package dev.kunaal.ratings_view

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.view.*
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import java.util.*
import kotlin.collections.set
import kotlin.math.max
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
@JvmOverloads constructor(context: Context,
                          attrs: AttributeSet? = null,
                          defStyleAttr: Int = 0)
    : View(context, attrs, defStyleAttr) {

    private var primaryColor: Int = 0

    private var animatedRating = 0
    private var currenArcLength = 0F

    // Arc
    private var lastColor = 0
    private var arcPaint = Paint()
    private var arcColor: Int = ContextCompat.getColor(context, android.R.color.black)
    private var arcWidthScale = 1F
    private val oval = RectF(0F, 0F, width.toFloat(), height.toFloat())

    // Bg
    private var bgPaint = Paint()
    private var bgColor: Int = ContextCompat.getColor(context, android.R.color.black)

    // Text
    private var textBounds = Rect()
    private val textPaint = Paint()
    private var textColor: Int = ContextCompat.getColor(context, android.R.color.black)

    // Color ranges map indicating the color to be displayed at percent
    private var colorRangeMap = TreeMap<Int, Int>()

    // Animation
    private var arcAnimator = ValueAnimator()
    private var numberAnimator = ValueAnimator()
    private var primaryAnimatorSet = AnimatorSet()

    // Loading
    private var isLoading = false
    private var loadingAnimSet = AnimatorSet()
    private var loadingAngleAnimator = ValueAnimator()
    private var loadingLengthAnimator = ValueAnimator()

    /**
     * Rating number to display.
     *
     * Arc percentage is based off of this value.
     * Number should be between 0 - 100
     * Setting the value will automatically update view.
     */
    var rating = 0
        set(value) {
            field = max(0, min(value, 100))
            if (!isLoading)
                startAnimation()
        }

    /**
     * Number to scale rating number by
     *
     * Number should be between 0F - 2F
     * Setting the scale will automatically update view.
     */
    var textScale: Float = 1F
        set(value) {
            field = max(0F, min(value, 2F))
            changeRatingTextSize(getRatingTextSize(field))
        }

    /**
     * Changes default background color
     *
     * @param color color to set background to
     */
    fun setBgColor(color: Int) {
        bgColor = color
        changeBgColor(color)
    }

    /**
     * Changes default arc color
     * If threshold colors are enabled, this will be override on next update
     *
     * @param color color to set arc to
     */
    fun setArcColor(color: Int) {
        arcColor = color
        changeArcColor(color)
    }

    /**
     * Changes default text color
     *
     * @param color color to set text to
     */
    fun setTextColor(color: Int) {
        textColor = color
        changeTextColor(color)
    }

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
        changeArcColor(colorRangeMap.floorEntry(rating)!!.value)
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
        changeArcColor(colorRangeMap.floorEntry(rating)!!.value)
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
            changeArcColor(arcColor)
        else
            changeArcColor(colorRangeMap.floorEntry(rating)!!.value)
    }

    /**
     * Removes all threshold colors for the arc
     */
    fun removeAllArcThresholdColor() {
        colorRangeMap.clear()
        changeArcColor(arcColor)
    }

    /**
     * Scales arc width based on view width
     * Default scale value is 1F
     *
     * @param scale number to scale arc width by
     */
    fun setArcWidthScale(scale: Float) {
        arcWidthScale = scale
        changeArcWidth(arcWidthScale)
    }

    /**
     * Toggles loading animation based on current loading state.
     *
     * If current state is not `loading`, startLoadingAnimation() is called
     * @see dev.kunaal.ratings_view.RatingsView.startLoadingAnimation
     *
     * If current state is `loading`, stopLoadingAnimation() is called
     * @see dev.kunaal.ratings_view.RatingsView.stopLoadingAnimation
     *
     */
    fun toggleLoadingAnimation() {
        if (isLoading)
            stopLoadingAnimation()
        else
            startLoadingAnimation()
    }

    private val resetArcLengthAnimator = ValueAnimator()

    /**
     * Starts loading animation and hides rating number
     */
    fun startLoadingAnimation() {
        if (loadingAnimSet.isRunning)
            loadingAnimSet.cancel()

        isLoading = true

        arcPaint.color = primaryColor

        resetArcLengthAnimator.apply {
            setFloatValues(currenArcLength, 25F)
            addUpdateListener {
                currenArcLength = animatedValue as Float
                postInvalidate()
            }
        }

        loadingAngleAnimator.apply {
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            setFloatValues(startAngle, startAngle + 360F)
            addUpdateListener {
                startAngle = animatedValue as Float
                postInvalidate()
            }
        }

        loadingLengthAnimator.apply {
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            setFloatValues(25F, 150F)
            addUpdateListener {
                currenArcLength = animatedValue as Float
                postInvalidate()
            }
        }

        loadingAnimSet.apply {
            duration = 500
            interpolator = LinearInterpolator()
            play(resetArcLengthAnimator)
                    .before(loadingAngleAnimator)
            play(loadingAngleAnimator)
                    .with(loadingLengthAnimator)
            start()
        }
    }

    private val stopLoadingAnimSet = AnimatorSet()
    private val stopLoadingAngleAnimator = ValueAnimator()
    private val stopLoadingArcLengthAnimator = ValueAnimator()

    /**
     * Stops loading animation and restores rating value and state
     */
    fun stopLoadingAnimation() {
        if (loadingAnimSet.isRunning)
            loadingAnimSet.cancel()

        if (stopLoadingAnimSet.isRunning) {
            stopLoadingAnimSet.removeAllListeners()
            stopLoadingAnimSet.cancel()
        }


        stopLoadingAngleAnimator.apply {
            repeatCount = 0
            setFloatValues(startAngle, 270F)
            addUpdateListener {
                startAngle = animatedValue as Float
                postInvalidate()
            }
        }

        stopLoadingArcLengthAnimator.apply {
            repeatCount = 0
            setFloatValues(currenArcLength, 1F)
            addUpdateListener {
                currenArcLength = animatedValue as Float
                postInvalidate()
            }
        }

        stopLoadingAnimSet.apply {
            duration - 500
            play(stopLoadingAngleAnimator)
                    .with(stopLoadingArcLengthAnimator)
            start()
            doOnEnd {
                isLoading = false
                removeAllListeners()
                if (colorRangeMap.isNotEmpty())
                    arcPaint.color = colorRangeMap[0]!!
                startAnimation()
            }
        }
    }

    /*********************************** Private **********************************/

    init {
        isSaveEnabled = true

        val primValue = TypedValue()
        context.theme.resolveAttribute(R.attr.colorPrimary, primValue, true)
        primaryColor = primValue.data

        val bgValue = TypedValue()
        context.theme.resolveAttribute(R.attr.background, primValue, true)
        val defaultBgColor = bgValue.data

        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.RatingsView, 0, 0)
            arcColor = array.getColor(R.styleable.RatingsView_arcColor, primaryColor)
            bgColor = array.getColor(R.styleable.RatingsView_bgColor, defaultBgColor)
            textColor = array.getColor(R.styleable.RatingsView_textColor, primaryColor)
            doOnNextLayout {
                if(textScale == 0F)
                    changeRatingTextSize(
                            getRatingTextSize(array.getFloat(R.styleable.RatingsView_textScale, 1F)),
                            true
                    )
                else
                    changeRatingTextSize(
                            getRatingTextSize(textScale),
                            true
                    )

                // Get arcWidthScale from attribute, if nothing to restore
                if(arcWidthScale == 1F)
                    arcWidthScale =
                            array.getFloat(R.styleable.RatingsView_arcWidthScale, arcWidthScale)

                changeArcWidth(arcWidthScale)
                array.recycle()
            }
        }

        startAnimation()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        val topPoint = (width / 2F) - (measuredWidth / 2F)
        val botPoint = (height / 2F) - (measuredHeight / 2F)
        oval.set(topPoint + paddingLeft + marginLeft,
                botPoint + paddingTop + marginTop,
                topPoint + measuredWidth - paddingRight - marginRight,
                botPoint + measuredHeight - paddingBottom - marginBottom)

        arcPaint.apply {
            isAntiAlias = true
            color = arcColor
            strokeWidth = getStandardArcWidth() * arcWidthScale
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
            textSize = getRatingTextSize(textScale)
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            getTextBounds(rating.toString(), 0, rating.toString().length, textBounds)
        }
    }

    private var startAngle = 270F

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        with(canvas) {
            drawArc(oval, 0F, 360F, true, bgPaint)
            drawArc(oval, startAngle, -currenArcLength, false, arcPaint)
            if (!isLoading)
                drawText(
                        animatedRating.toString(),
                        oval.centerX(),
                        oval.centerY() + textBounds.height() / 2F,
                        textPaint
                )
            else
                animatedRating = 0
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val size = min(widthSize, heightSize)
        val paddingSize = (size * 0.125).toInt()
        setPadding(paddingSize, paddingSize, paddingSize, paddingSize)
        setMeasuredDimension(size, size)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        arcAnimator.removeAllUpdateListeners()
        numberAnimator.removeAllUpdateListeners()
    }

    override fun onSaveInstanceState(): Parcelable? {
        val viewState = SavedState(super.onSaveInstanceState())
        viewState.rating = rating
        viewState.arcColor = arcColor
        viewState.arcWidthScale = arcWidthScale
        viewState.bgColor = bgColor
        viewState.textColor = textColor
        viewState.textScale = textScale
        viewState.thresholdColorsMap = colorRangeMap
        return viewState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val savedState = (state as SavedState)
        super.onRestoreInstanceState(savedState.superState)

        rating = savedState.rating
        arcColor = savedState.arcColor
        arcWidthScale = savedState.arcWidthScale
        bgColor = savedState.bgColor
        textColor = savedState.textColor
        textScale = savedState.textScale
        colorRangeMap = savedState.thresholdColorsMap as TreeMap<Int, Int>

        // Apply first color from colorRangeMap to avoid starting color from default one
        if(colorRangeMap.isNotEmpty())
            arcColor = colorRangeMap[0]!!

        requestLayout()
    }

    /**
     * Starts arc and number animation.
     * Number animation also calls startColorAnimation() if required
     */
    private fun startAnimation() {
        if (primaryAnimatorSet.isRunning)
            primaryAnimatorSet.cancel()

        arcAnimator.apply {
            setFloatValues(currenArcLength, 360 * rating * 0.01F)
            addUpdateListener {
                currenArcLength = animatedValue as Float
                postInvalidate()
            }
        }

        numberAnimator.apply {
            setIntValues(animatedRating, rating)
            interpolator = FastOutSlowInInterpolator()
            addUpdateListener {
                animatedRating = animatedValue as Int
                if (colorRangeMap.isNotEmpty() && colorRangeMap.floorKey(animatedRating)!! != lastColor) {
                    lastColor = colorRangeMap.floorKey(animatedRating)
                    changeArcColor(colorRangeMap[lastColor]!!)
                }
                postInvalidate()
            }
        }

        primaryAnimatorSet.apply {
            duration = 1000
            interpolator = FastOutSlowInInterpolator()
            playTogether(arcAnimator, numberAnimator)
            start()
        }
    }

    private var arcColorAnimator = ValueAnimator()

    /**
     * Starts animating arc color to given color
     *
     * @param toColor color to animate to
     */
    private fun changeArcColor(toColor: Int) {
        if (isLoading)
            return
        if (arcColorAnimator.isRunning)
            arcColorAnimator.cancel()

        arcColorAnimator.apply {
            setIntValues(arcPaint.color, toColor)
            setEvaluator(ArgbEvaluator())
            addUpdateListener {
                arcPaint.color = animatedValue as Int
                postInvalidate()
            }
            start()
        }
    }

    private var bgColorAnimator = ValueAnimator()
    private fun changeBgColor(toColor: Int) {
        if (bgColorAnimator.isRunning)
            bgColorAnimator.cancel()

        bgColorAnimator.apply {
            setIntValues(bgPaint.color, toColor)
            setEvaluator(ArgbEvaluator())
            addUpdateListener {
                bgPaint.color = animatedValue as Int
                postInvalidate()
            }
            start()
        }
    }

    private var textColorAnimator = ValueAnimator()
    private fun changeTextColor(toColor: Int) {
        if (textColorAnimator.isRunning)
            textColorAnimator.cancel()

        textColorAnimator.apply {
            setIntValues(textPaint.color, toColor)
            setEvaluator(ArgbEvaluator())
            addUpdateListener {
                textPaint.color = animatedValue as Int
                postInvalidate()
            }
            start()
        }
    }

    private fun getRatingTextSize(forScale: Float): Float {
        return ((measuredWidth - paddingLeft - marginLeft - paddingRight - marginRight) * 0.4F) * forScale
    }

    private var textSizeAnimator = ValueAnimator()
    private fun changeRatingTextSize(toSize: Float, enableLongAnim: Boolean = false) {
        if (textSizeAnimator.isRunning)
            textSizeAnimator.cancel()

        textSizeAnimator.apply {
            setFloatValues(textPaint.textSize, toSize)
            duration = if (enableLongAnim)
                1000
            else
                300 // default
            addUpdateListener {
                textPaint.apply {
                    textSize = animatedValue as Float
                    getTextBounds(rating.toString(), 0, rating.toString().length, textBounds)
                }
                postInvalidate()
            }
            start()
        }
    }

    private fun getStandardArcWidth(): Float = measuredWidth * 0.1F

    private var arcWidthAnimator = ValueAnimator()
    private fun changeArcWidth(withScale: Float) {
        if (arcWidthAnimator.isRunning)
            arcWidthAnimator.cancel()

        arcWidthAnimator.apply {
            setFloatValues(arcPaint.strokeWidth, max(0F, getStandardArcWidth() * withScale))

            addUpdateListener {
                arcPaint.strokeWidth = animatedValue as Float
                postInvalidate()
            }
            start()
        }
    }
}
