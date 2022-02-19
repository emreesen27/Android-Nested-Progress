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
import com.sn.lib.Constants.ANIM_DURATION
import com.sn.lib.Constants.CIRCLE_RADIUS
import com.sn.lib.Constants.COLOR_BLUE
import com.sn.lib.Constants.COLOR_LIGHT_BLUE
import com.sn.lib.Constants.INNER_ANIM_INTERPOLATOR
import com.sn.lib.Constants.INNER_LOADER_POS
import com.sn.lib.Constants.INNER_LOADER_LENGTH
import com.sn.lib.Constants.INNER_STROKE_WIDTH
import com.sn.lib.Constants.OUTER_ANIM_INTERPOLATOR
import com.sn.lib.Constants.OUTER_LOADER_POS
import com.sn.lib.Constants.OUTER_LOADER_LENGTH
import com.sn.lib.Constants.OUTER_STROKE_WIDTH
import com.sn.lib.Constants.SIZE_FACTOR
import java.lang.IllegalArgumentException
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * @author Aydin Emre E.
 * @version 1.0.1
 * @since 19-02-2022
 */

class NestedProgress @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


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


    @Suppress("UNUSED_VARIABLE")
    private var innerLoaderAnimDuration: Int = ANIM_DURATION

    @Suppress("UNUSED_VARIABLE")
    private var outerLoaderAnimDuration: Int = ANIM_DURATION

    @Suppress("UNUSED_VARIABLE")
    var innerAnimInterpolator = INNER_ANIM_INTERPOLATOR

    @Suppress("UNUSED_VARIABLE")
    var outerAnimInterpolator = OUTER_ANIM_INTERPOLATOR

    /** There is no limit value for stroke width, but correct values should be used for a smooth display * */
    @Suppress("UNUSED_VARIABLE")
    var innerLoaderStrokeWidth: Float = INNER_STROKE_WIDTH

    @Suppress("UNUSED_VARIABLE")
    var outerLoaderStrokeWidth: Float = OUTER_STROKE_WIDTH

    @ColorInt
    @Suppress("UNUSED_VARIABLE")
    var innerLoaderColor: Int = COLOR_LIGHT_BLUE

    @ColorInt
    @Suppress("UNUSED_VARIABLE")
    var outerLoaderColor: Int = COLOR_BLUE

    /** The maximum angle at which you can see a movement in the animation is 359
     * -innerLoaderLength
     * -outerLoaderLength
     * **/

    @Suppress("UNUSED_VARIABLE")
    var innerLoaderLength: Float = INNER_LOADER_LENGTH

    @Suppress("UNUSED_VARIABLE")
    var outerLoaderLength: Float = OUTER_LOADER_LENGTH

    /** The library ignores the dp value so you should keep the sizeFactor value range 1 and 3.
     *  In case you exceed value you will get IllegalArgumentException
     * @throws IllegalArgumentException
     * */
    @Suppress("UNUSED_VARIABLE")
    var sizeFactor: Float = SIZE_FACTOR
        set(value) {
            field =
                if (value > 3.0f && value < 1.0f) throw IllegalArgumentException("sizeFactor Must be range 1.0 and 3.0") else value
        }

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
            attributes.getFloat(
                R.styleable.NestedProgress_innerLoaderStrokeWidth,
                this.innerLoaderStrokeWidth
            )

        outerLoaderStrokeWidth =
            attributes.getFloat(
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

        sizeFactor = attributes.getFloat(R.styleable.NestedProgress_sizeFactor, this.sizeFactor)

        attributes.recycle()

        innerLoaderAnimation()
        outerLoaderAnimation()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerW: Float = width / 2f
        val centerH: Float = height / 2f

        outerLoadingRect.set(
            centerW - (INNER_LOADER_POS * sizeFactor),
            centerH - (INNER_LOADER_POS * sizeFactor),
            centerW + (INNER_LOADER_POS * sizeFactor),
            centerH + (INNER_LOADER_POS * sizeFactor)
        )

        innerLoadingRect.set(
            centerW - (OUTER_LOADER_POS * sizeFactor),
            centerH - (OUTER_LOADER_POS * sizeFactor),
            centerW + (OUTER_LOADER_POS * sizeFactor),
            centerH + (OUTER_LOADER_POS * sizeFactor)
        )

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

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val desiredWidth = (250 * sizeFactor).roundToInt()
        val desiredHeight = (250 * sizeFactor).roundToInt()

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> {
                widthSize
            }
            MeasureSpec.AT_MOST -> {
                min(desiredWidth, widthSize)
            }
            else -> {
                desiredWidth
            }
        }

        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> {
                heightSize
            }
            MeasureSpec.AT_MOST -> {
                min(desiredHeight, heightSize)
            }
            else -> {
                desiredHeight
            }
        }

        setMeasuredDimension(width, height)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnimation()
    }

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

    private fun updatePaint(color: Int, strokeWidth: Float) {
        paint.color = color
        paint.strokeWidth = strokeWidth
    }

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
