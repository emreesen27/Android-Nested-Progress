package com.sn.lib

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
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
import kotlin.math.min

class NestedProgress @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    private var innerLoaderAnimDuration: Int = ANIM_DURATION
    private var outerLoaderAnimDuration: Int = ANIM_DURATION

    private var innerLoaderAnimValue: Int = 0
    private var outerLoaderAnimValue: Int = 0

    private var innerAnimInterpolator = INNER_ANIM_INTERPOLATOR
    private var outerAnimInterpolator = OUTER_ANIM_INTERPOLATOR

    private val innerLoaderAnimator = ValueAnimator.ofInt(1, CIRCLE_RADIUS)
    private val outerLoaderAnimator = ValueAnimator.ofInt(1, CIRCLE_RADIUS)

    private var innerLoaderLength: Float = INNER_LOADER_LENGTH
    private var outerLoaderLength: Float = OUTER_LOADER_LENGTH

    private var innerLoaderStrokeWidth: Float = INNER_STROKE_WIDTH
    private var outerLoaderStrokeWidth: Float = OUTER_STROKE_WIDTH

    private var innerLoaderColor: Int = COLOR_BLUE
    private var outerLoaderColor: Int = COLOR_LIGHT_BLUE

    private val paint = Paint().apply {
        this.style = Paint.Style.STROKE
        this.isAntiAlias = true
    }

    private val innerLoadingRect: RectF = RectF()
    private val outerLoadingRect: RectF = RectF()

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


        attributes.recycle()

        innerLoaderAnimation()
        outerLoaderAnimation()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerW: Float = width / 2f
        val centerH: Float = height / 2f

        innerLoadingRect.set(
            centerW - INNER_LOADER_POS,
            centerH - INNER_LOADER_POS,
            centerW + INNER_LOADER_POS,
            centerH + INNER_LOADER_POS
        )
        outerLoadingRect.set(
            centerW - OUTER_LOADER_POS,
            centerH - OUTER_LOADER_POS,
            centerW + OUTER_LOADER_POS,
            centerH + OUTER_LOADER_POS
        )

        updatePaint(outerLoaderColor, outerLoaderStrokeWidth)
        canvas.drawArc(
            outerLoadingRect,
            outerLoaderAnimValue.toFloat(),
            innerLoaderLength,
            false,
            paint
        )

        updatePaint(innerLoaderColor, innerLoaderStrokeWidth)
        canvas.drawArc(
            innerLoadingRect,
            innerLoaderAnimValue.toFloat(),
            outerLoaderLength,
            false,
            paint
        )

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val desiredWidth = 300
        val desiredHeight = 300

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

    private fun innerLoaderAnimation() {
        innerLoaderAnimator.interpolator = OvershootInterpolator()
        innerLoaderAnimator.duration = innerLoaderAnimDuration.toLong()
        innerLoaderAnimator.repeatCount = ValueAnimator.INFINITE
        innerLoaderAnimator.addUpdateListener { animation ->
            innerLoaderAnimValue = animation.animatedValue as Int
            invalidate()
        }
    }

    private fun outerLoaderAnimation() {
        outerLoaderAnimator.interpolator = LinearInterpolator()
        outerLoaderAnimator.duration = outerLoaderAnimDuration.toLong()
        outerLoaderAnimator.repeatCount = ValueAnimator.INFINITE
        outerLoaderAnimator.addUpdateListener { animation ->
            outerLoaderAnimValue = animation.animatedValue as Int
            invalidate()
        }
    }


}
