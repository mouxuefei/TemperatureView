package com.exmple.miclockview;

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class TemperatureView @JvmOverloads
constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    //画外部的圆的画笔
//    private val mOutCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    //画内部圆的画笔
    private val mInCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    //画外边缘的线的画笔
    private val mGrayLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    //画温度文字的画笔
    private val mTempTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    //
    private val mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    //画进度的画笔1
    private val mLineProgressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mProgressCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    //小球
    private val mProgressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    //画三角形
    private val mPaintTriangle = Paint(Paint.ANTI_ALIAS_FLAG)
    // 控件宽
    private var mWidth: Int = 0
    // 控件高
    private var mHeight: Int = 0
    // 刻度盘半径
    private var dialRadius: Int = 0
    // 中间圆半径
    private var arcRadius: Int = 0
    private var mBgRectf: RectF? = null
    private var currentAngle: Float = 0f
    // 当前按钮旋转的角度
    private var rotateAngle: Float = 0.toFloat()
    //初始化温度值
    private var temText: Int = 15
    private var isDown: Boolean = false
    private var isMove: Boolean = false
    private var downX: Float = 0f
    private var downY: Float = 0f

    init {
        //关闭硬件加速
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        mInCirclePaint.color = Color.WHITE
        mInCirclePaint.strokeWidth = 1f
        mInCirclePaint.style = Paint.Style.FILL
        mInCirclePaint.setShadowLayer(25f, 0f, 0f, Color.GRAY)


        mGrayLinePaint.color = Color.parseColor("#00EEEE")
        mGrayLinePaint.strokeWidth = dp2px(1f).toFloat()
        mGrayLinePaint.style = Paint.Style.STROKE
        mGrayLinePaint.setShadowLayer(15f, 0f, 0f, Color.GRAY)

        mTempTextPaint.textSize = sp2px(32f).toFloat()
        mTempTextPaint.style = Paint.Style.FILL
        mTempTextPaint.color = Color.parseColor("#7CCD7C")
        mTempTextPaint.textAlign = Paint.Align.CENTER
        mTempTextPaint.isFakeBoldText = true
        mTempTextPaint.setShadowLayer(5f, 0f, 0f, Color.GRAY)

        mTextPaint.textSize = sp2px(18f).toFloat()
        mTextPaint.style = Paint.Style.FILL
        mTextPaint.color = Color.parseColor("#aaaaaa")
        mTextPaint.textAlign = Paint.Align.CENTER
//        mTextPaint.setShadowLayer(5f, 0f, 0f, Color.GRAY)

        mLineProgressPaint.style = Paint.Style.STROKE
        mLineProgressPaint.strokeWidth = dp2px(10f).toFloat()
        mLineProgressPaint.color = Color.parseColor("#7CCD7C")
//        mLineProgressPaint.setShadowLayer(5f, 0f, 0f, Color.GRAY)

        mProgressCirclePaint.style = Paint.Style.FILL
//        mProgressCirclePaint.setShadowLayer(15f, 0f, 0f, Color.GRAY)

        mProgressPaint.style = Paint.Style.FILL
        mProgressPaint.color = Color.parseColor("#436EEE")
        mProgressPaint.strokeCap = Paint.Cap.ROUND
//        mProgressPaint.setShadowLayer(5f, 0f, 0f, Color.GRAY)

        mPaintTriangle.style = Paint.Style.FILL
        mPaintTriangle.color = Color.parseColor("#436EEE")

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val height = View.MeasureSpec.getSize(heightMeasureSpec)
        val imageSize = if (width < height) width else height
        setMeasuredDimension(imageSize, imageSize)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mHeight = Math.min(h, w)
        mWidth = height
        dialRadius = ((width / 2 - dp2px(10f)).toInt())
        arcRadius = dialRadius / 2
        mBgRectf = RectF(-dialRadius * 1f / 2 - dp2px(5f), -dialRadius * 1f / 2 - dp2px(5f), dialRadius * 1f / 2 + dp2px(5f), dialRadius * 1f / 2 + dp2px(5f))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.translate(mWidth * 1f / 2, mHeight * 1f / 2);
        drawInCircle(canvas)
        drawLine(canvas)
        drawText(canvas)
        drawProgress(canvas)
        drawPointCircle(canvas)
    }

    private val drawPointCircle = { canvas: Canvas ->
        canvas.save()
        canvas.rotate(rotateAngle + 2)
        mProgressPaint.setARGB(255, (0.7 * currentAngle).toInt(), (260 - 0.7 * currentAngle).toInt(), (260 - 0.7 * currentAngle).toInt())
        mPaintTriangle.setARGB(255, (0.7 * currentAngle).toInt(), (260 - 0.7 * currentAngle).toInt(), (260 - 0.7 * currentAngle).toInt())
        if (temText == 30 || temText == 29) {
            mProgressPaint.setARGB(255, 255, 0, 0)
            mPaintTriangle.setARGB(255, 255, 0, 0)
        } else if (temText == 15) {
            mProgressPaint.setARGB(255, 0, 255, 255)
            mPaintTriangle.setARGB(255, 0, 255, 255)
        }
        val path = Path()
        path.moveTo(dialRadius * 1f / 2 + dp2px(5f), (-dp2px(10f)).toFloat())
        path.lineTo(dialRadius * 1f / 2 + dp2px(5f), dp2px(10f).toFloat())
        path.lineTo(dialRadius * 1f / 2 - dp2px(10f), 0f)
        path.close()
        canvas.drawPath(path, mPaintTriangle)
        val point = dialRadius * 1f / 2 + dp2px(5f)
        canvas.drawCircle(point, 0f, dp2px(10f).toFloat(), mProgressPaint)
        canvas.restore()
    }

    private val drawProgress = { canvas: Canvas ->
        if (rotateAngle > 0) {
            mProgressCirclePaint.color = Color.parseColor("#00ffff")
            canvas.drawCircle(dialRadius * 1f / 2 + dp2px(5f), 0f, dp2px(5f).toFloat(), mProgressCirclePaint)
        }
        val colors = intArrayOf(Color.parseColor("#00ffff"), Color.parseColor("#ff0000"))
        val mShader = SweepGradient(0f, 0f, colors, null)
        mLineProgressPaint.shader = mShader
        canvas.drawArc(mBgRectf, 0f, rotateAngle, false, mLineProgressPaint)
    }

    private val drawText = { canvas: Canvas ->
        val baseLineY = Math.abs(mTempTextPaint.ascent() + mTempTextPaint.descent()) / 2
        mTempTextPaint.setARGB(180, (0.7 * currentAngle).toInt(), (255 - 0.7 * currentAngle).toInt(), (255 - 0.7 * currentAngle).toInt())
        if (temText == 30 || temText == 29) {
            mTempTextPaint.setARGB(255, 255, 0, 0)
        } else if (temText == 15) {
            mTempTextPaint.setARGB(255, 0, 255, 255)
        }
        canvas.drawText("${temText}°", 0f, baseLineY + dp2px(20f), mTempTextPaint)
        canvas.drawText("最大温度设置", 0f, baseLineY - dp2px(20f), mTextPaint)
    }

    private var beginAngle = 0f
    private val drawLine = { canvas: Canvas ->
        beginAngle = 0f
        canvas.save()
        for (i in 0..180) {
            canvas.save()
            canvas.rotate(beginAngle)
            mGrayLinePaint.setARGB(180, (0.7 * i * 2).toInt(), ((255 - 0.7 * i * 2).toInt()), ((255 - 0.7 * i * 2).toInt()))
            if (beginAngle % 12 == 0f) {
                canvas.drawLine(dialRadius * 7f / 9 - dp2px(5f), 0f, dialRadius * 8f / 9, 0f, mGrayLinePaint)
            } else {
                canvas.drawLine(dialRadius * 7f / 9, 0f, dialRadius * 8f / 9, 0f, mGrayLinePaint)
            }
            beginAngle += 2f
            canvas.restore()
        }
        canvas.restore()
    }

    private val drawInCircle = { canvas: Canvas ->
        canvas.drawCircle(0f, 0f, arcRadius.toFloat(), mInCirclePaint)
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isDown = true
                downX = event.x
                downY = event.y
                currentAngle = calcAngle(downX, downY)
            }

            MotionEvent.ACTION_MOVE -> {
                isMove = true
                val targetX: Float = event.x
                val targetY: Float = event.y
                val angle = calcAngle(targetX, targetY)
                // 滑过的角度增量
                var angleIncreased = angle - currentAngle
                if (angleIncreased < -180) {
                    angleIncreased += 360
                } else if (angleIncreased > 180) {
                    angleIncreased -= 360
                }
                increaseAngle(angleIncreased)
                currentAngle = angle
                invalidate()
                // 滑过的角度增量
            }

            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                if (isDown) {
                    if (isMove) {
                        temparetureChangeListner?.invoke(temText)
                        isMove = false
                    }
                    isDown = false
                }
            }

        }
        return true
    }

    /**
     * 增加旋转角度
     */
    private val increaseAngle = { angle: Float ->
        rotateAngle += angle
        if (rotateAngle < 0) {
            rotateAngle = 0f
        } else if (rotateAngle > 360f) {
            rotateAngle = 360f
        }
        temText = (rotateAngle / 360 * 15).toInt() + 15
    }

    /**
     * 以按钮圆心为坐标圆点，建立坐标系，求出(targetX, targetY)坐标与x轴的夹角
     *
     * @param targetX x坐标
     * @param targetY y坐标
     * @return (targetX, targetY)坐标与x轴的夹角
     */
    private val calcAngle = { targetX: Float, targetY: Float ->
        val x = targetX - width / 2
        val y = targetY - height / 2
        val radian: Double

        if (x != 0f) {
            val tan = Math.abs(y / x)
            if (x > 0) {
                if (y >= 0) {
                    radian = Math.atan(tan.toDouble())
                } else {
                    radian = 2 * Math.PI - Math.atan(tan.toDouble())
                }
            } else {
                if (y >= 0) {
                    radian = Math.PI - Math.atan(tan.toDouble())
                } else {
                    radian = Math.PI + Math.atan(tan.toDouble())
                }
            }
        } else {
            if (y > 0) {
                radian = Math.PI / 2
            } else {
                radian = -Math.PI / 2
            }
        }
        (radian * 180 / Math.PI).toFloat()
    }
    var temparetureChangeListner: ((Int) -> Unit)? = null
}
/**
 * dp转px
 */
fun View.dp2px(dipValue: Float): Float {
    return (dipValue * this.resources.displayMetrics.density + 0.5f)
}

/**
 * px转dp
 */
fun View.px2dp(pxValue: Float): Float {
    return (pxValue / this.resources.displayMetrics.density + 0.5f)
}

/**
 * sp转px
 */
fun View.sp2px(spValue: Float): Float {
    return (spValue * this.resources.displayMetrics.scaledDensity + 0.5f)
}