package com.sn.lib

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.*
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import com.sn.lib.Constants.ANIM_DURATION
import com.sn.lib.Constants.CIRCLE_RADIUS
import com.sn.lib.Constants.COLOR_BLUE
import com.sn.lib.Constants.COLOR_LIGHT_BLUE
import com.sn.lib.Constants.DESIRED_WH
import com.sn.lib.Constants.INNER_ANIM_INTERPOLATOR
import com.sn.lib.Constants.INNER_LOADER_LENGTH
import com.sn.lib.Constants.INNER_STROKE_WIDTH
import com.sn.lib.Constants.MAX_B_CIRCLES
import com.sn.lib.Constants.MAX_STROKE
import com.sn.lib.Constants.MAX_TOTAL_STROKE
import com.sn.lib.Constants.MID_POINT
import com.sn.lib.Constants.MIN_B_CIRCLES
import com.sn.lib.Constants.MIN_STOKE
import com.sn.lib.Constants.OUTER_ANIM_INTERPOLATOR
import com.sn.lib.Constants.OUTER_LOADER_LENGTH
import com.sn.lib.Constants.OUTER_STROKE_WIDTH
import com.sn.lib.Constants.SPACE_BETWEEN_CIRCLES
import com.sn.lib.Constants.START_POINT
import com.sn.lib.ext.dp
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * @author Aydin Emre E.
 * @version 1.0.2
 * @since 19-02-2022
 */

@Suppress("unused", "MemberVisibilityCanBePrivate")
class NestedProgress @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /**
     * Default values are defined here. If a new one is not assigned, these values are used.
     * @see [Constants]
     */

    // Animation value of progressions. These values are assigned to the "sweep angle" value in drawArc
    private var innerLoaderAnimValue: Int = 0
    private var outerLoaderAnimValue: Int = 0

    // Animation always starts at 1 and goes full round until 360
    private val innerLoaderAnimator = ValueAnimator.ofInt(1, CIRCLE_RADIUS)
    private val outerLoaderAnimator = ValueAnimator.ofInt(1, CIRCLE_RADIUS)

    private val innerLoadingRect: RectF = RectF()
    private val outerLoadingRect: RectF = RectF()

    private val paint = Paint().apply {
        this.style = Paint.Style.STROKE
        this.isAntiAlias = true
    }

    // Animation times for both progresses
    var innerLoaderAnimDuration: Int = ANIM_DURATION
    var outerLoaderAnimDuration: Int = ANIM_DURATION

    // Animation types for both progresses
    var innerAnimInterpolator = INNER_ANIM_INTERPOLATOR
    var outerAnimInterpolator = OUTER_ANIM_INTERPOLATOR

    @ColorInt
    var innerLoaderColor: Int = COLOR_LIGHT_BLUE

    @ColorInt
    var outerLoaderColor: Int = COLOR_BLUE

    /** You must value between the drawing angles of the circle (1-359), with value of 0 and 360, the animation is not visible.
     * - innerLoaderLength
     * - outerLoaderLength
     */
    var innerLoaderLength: Float = INNER_LOADER_LENGTH
    var outerLoaderLength: Float = OUTER_LOADER_LENGTH

    /** Strokes can be in the range 1 to 10, otherwise the illegal argument exception error is thrown.
     * @throws IllegalArgumentException
     * - innerLoaderStrokeWidth
     * - outerLoaderStrokeWidth
     * @see [dp] An extensions written for float. It is used to convert the default value to dp.
     */
    @Dimension
    var innerLoaderStrokeWidth: Float = INNER_STROKE_WIDTH.dp
        set(value) {
            field =
                if (value > MAX_STROKE.dp || value < MIN_STOKE.dp) throw IllegalArgumentException(
                    resources.getString(
                        R.string.stroke_range_error
                    )
                ) else value
        }

    @Dimension
    var outerLoaderStrokeWidth: Float = OUTER_STROKE_WIDTH.dp
        set(value) {
            field =
                if (value > MAX_STROKE.dp || value < MIN_STOKE.dp) throw IllegalArgumentException(
                    resources.getString(
                        R.string.stroke_range_error
                    )
                ) else value
        }

    /** Set the distance between the two loaders. The higher this value, the smaller the "internal loader".
     *  Space between circles can be in the range 1 to 10, otherwise the illegal argument exception error is thrown.
     * @throws IllegalArgumentException
     * - spaceBetweenCircles
     */
    @Dimension
    var spaceBetweenCircles = SPACE_BETWEEN_CIRCLES.dp
        set(value) {
            field =
                if (value > MAX_B_CIRCLES.dp || value < MIN_B_CIRCLES.dp) throw IllegalArgumentException(
                    resources.getString(
                        R.string.space_between_range_error
                    )
                ) else value
        }

    /**
     * You can find the attributes from the attrs.xml file.
     */
    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.NestedProgress)

        innerLoaderColor =
            attributes.getColor(R.styleable.NestedProgress_innerLoaderColor, this.innerLoaderColor)

        outerLoaderColor =
            attributes.getColor(R.styleable.NestedProgress_outerLoaderColor, this.outerLoaderColor)

        innerLoaderLength =
            attributes.getFloat(
                R.styleable.NestedProgress_innerLoaderLength,
                this.innerLoaderLength
            )
        outerLoaderLength =
            attributes.getFloat(
                R.styleable.NestedProgress_outerLoaderLength,
                this.outerLoaderLength
            )

        innerLoaderAnimDuration =
            attributes.getInt(
                R.styleable.NestedProgress_innerLoaderAnimDuration,
                this.innerLoaderAnimDuration
            )
        outerLoaderAnimDuration =
            attributes.getInt(
                R.styleable.NestedProgress_outerLoaderAnimDuration,
                this.outerLoaderAnimDuration
            )

        innerLoaderStrokeWidth =
            attributes.getDimension(
                R.styleable.NestedProgress_innerLoaderStrokeWidth,
                this.innerLoaderStrokeWidth
            )

        outerLoaderStrokeWidth =
            attributes.getDimension(
                R.styleable.NestedProgress_outerLoaderStrokeWidth,
                this.outerLoaderStrokeWidth
            )

        innerAnimInterpolator =
            attributes.getInt(
                R.styleable.NestedProgress_innerAnimInterpolator,
                this.innerAnimInterpolator
            )

        outerAnimInterpolator =
            attributes.getInt(
                R.styleable.NestedProgress_outerAnimInterpolator,
                this.outerAnimInterpolator
            )

        spaceBetweenCircles =
            attributes.getDimension(
                R.styleable.NestedProgress_spaceBetweenCircles,
                this.spaceBetweenCircles
            )

        attributes.recycle()

        innerLoaderAnimation()
        outerLoaderAnimation()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val desired = DESIRED_WH.dp.roundToInt()

        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> {
                widthSize
            }
            MeasureSpec.AT_MOST -> {
                min(desired, widthSize)
            }
            else -> {
                desired
            }
        }

        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> {
                heightSize
            }
            MeasureSpec.AT_MOST -> {
                min(desired, heightSize)
            }
            else -> {
                desired
            }
        }

        setMeasuredDimension(width, height)

        val highStroke = outerLoaderStrokeWidth + innerLoaderStrokeWidth
        val expansion = MAX_TOTAL_STROKE.dp - highStroke

        outerLoadingRect.set(
            START_POINT + expansion + (highStroke / MID_POINT),
            START_POINT + expansion + (highStroke / MID_POINT),
            width - (expansion + (highStroke / MID_POINT)),
            width - (expansion + (highStroke / MID_POINT))
        )

        innerLoadingRect.set(
            START_POINT + (expansion + highStroke + spaceBetweenCircles),
            START_POINT + (expansion + highStroke + spaceBetweenCircles),
            width - (expansion + highStroke + spaceBetweenCircles),
            width - (expansion + highStroke + spaceBetweenCircles)
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        updatePaint(outerLoaderColor, outerLoaderStrokeWidth)
        canvas.drawArc(
            outerLoadingRect,
            outerLoaderAnimValue.toFloat(),
            outerLoaderLength,
            false,
            paint
        )

        updatePaint(innerLoaderColor, innerLoaderStrokeWidth)
        canvas.drawArc(
            innerLoadingRect,
            innerLoaderAnimValue.toFloat(),
            innerLoaderLength,
            false,
            paint
        )

    }

    // Starts the animation when the screen is attached
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnimation()
    }

    // Stops the animation when the screen is detached
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnimation()
    }

    private fun startAnimation() {
        innerLoaderAnimator.start()
        outerLoaderAnimator.start()
    }

    private fun stopAnimation() {
        innerLoaderAnimator.end()
        outerLoaderAnimator.end()
    }

    /**
     * Both progresses use the same paint object, which is updated with this function before being drawn.
     * @param color [Int]
     * @param strokeWidth [Float]
     */
    private fun updatePaint(color: Int, strokeWidth: Float) {
        paint.color = color
        paint.strokeWidth = strokeWidth
    }

    /**
     * Returns an interpolator back based on the integer value
     * sorting is the same as enum in attrs.xml.
     * @param interpolator [Int]
     */
    private fun getAnimation(interpolator: Int): Interpolator {
        return when (interpolator) {
            0 -> AccelerateInterpolator()
            1 -> DecelerateInterpolator()
            2 -> AccelerateDecelerateInterpolator()
            3 -> AnticipateInterpolator()
            4 -> AnticipateOvershootInterpolator()
            5 -> LinearInterpolator()
            6 -> OvershootInterpolator()
            else -> AccelerateDecelerateInterpolator()
        }
    }

    /**
     * Animation continues until progress is removed. Inherits its properties from defined variables.
     * - innerLoaderAnimation
     * - outerLoaderAnimation
     */
    private fun innerLoaderAnimation() {
        innerLoaderAnimator.interpolator = getAnimation(innerAnimInterpolator)
        innerLoaderAnimator.duration = innerLoaderAnimDuration.toLong()
        innerLoaderAnimator.repeatCount = ValueAnimator.INFINITE
        innerLoaderAnimator.addUpdateListener { animation ->
            innerLoaderAnimValue = animation.animatedValue as Int
            invalidate()
        }
    }

    private fun outerLoaderAnimation() {
        outerLoaderAnimator.interpolator = getAnimation(outerAnimInterpolator)
        outerLoaderAnimator.duration = outerLoaderAnimDuration.toLong()
        outerLoaderAnimator.repeatCount = ValueAnimator.INFINITE
        outerLoaderAnimator.addUpdateListener { animation ->
            outerLoaderAnimValue = animation.animatedValue as Int
            invalidate()
        }
    }

}
