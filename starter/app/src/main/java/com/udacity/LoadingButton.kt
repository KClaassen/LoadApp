package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var mBackgroundColor = 0
    private var mTextColor = 0


    private var paintButton = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.button_color)
    }


    private var paintLoadingButton = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.button_color_default)
    }

    private lateinit var buttonText: String
    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = resources.getDimension(R.dimen.default_text_size)
        textAlign = Paint.Align.CENTER
        color = Color.WHITE
    }

    // Paint object for coloring and styling
    private var paintCircle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.colorPrimaryDark)
    }

    private var valueAnimator = ValueAnimator()

    var value = 0.0f
    var width = 0.0f
    var sweepAngle = 0.0f


    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Clicked) { p, old, new ->

        // set the button text based on the state of the button
        buttonText = context.getString(buttonState.customButtonText)

        when (new) {
            ButtonState.Clicked->{
                paintCircle.color = context.getColor(R.color.colorAccent)
            }

            ButtonState.Loading -> {
                Log.d("LoadingButton", "ButtonState.Loading")
                paintCircle.color = context.getColor(R.color.colorPrimaryDark)
                valueAnimator =
                        ValueAnimator.ofFloat(0.0f, measuredWidth.toFloat()).setDuration(2000).apply {
                            addUpdateListener { valueAnimator ->
                                value = valueAnimator.animatedValue as Float
                                sweepAngle = value / 8
                                width = value * 5
                                invalidate()
                            }
                        }
                valueAnimator.start()
            }

            ButtonState.Completed-> {
                Log.d("LoadingButton", " ButtonState.Completed")
                valueAnimator.cancel()
                paintCircle.color = context.getColor(R.color.colorPrimaryDark)
                value = 0f
                invalidate()
            }
        }

    }


    init {
        buttonState = ButtonState.Clicked

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            mBackgroundColor = getColor(R.styleable.LoadingButton_backgroundColor, 0)
            mTextColor = getColor(R.styleable.LoadingButton_textColor, 0)
        }
    }

    /*Note: A Canvas instance comes as onDraw parameter to draw different shapes.
    Paint is for styling it. Ex, paint defines what color the shape will get.
     */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //Draw rectangle
        canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paintButton)
        canvas?.drawRect(0f, 0f, width, heightSize.toFloat(), paintLoadingButton)


        //Draw text
        val textHeight: Float = paintText.descent() - paintText.ascent()
        val textOffset: Float = textHeight / 2 - paintText.descent()
        canvas?.drawText(
                buttonText,
                widthSize.toFloat() / 2,
                heightSize.toFloat() / 2 + textOffset,
                paintText
        )

        canvas?.drawArc(
                widthSize - 145f,
                heightSize / 2 - 35f,
                widthSize - 75f,
                heightSize / 2 + 35f,
                0F,
                width,
                true,
                paintCircle
        )
    }

    fun loadingState(state: ButtonState) {
        buttonState = state
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
                MeasureSpec.getSize(w),
                heightMeasureSpec,
                0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}