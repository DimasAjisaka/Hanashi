package com.aji.hanashi.ui.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.aji.hanashi.R

class EmailView : AppCompatEditText, View.OnTouchListener {
    private lateinit var clearBtn: Drawable

    private fun init() {
        clearBtn = ContextCompat.getDrawable(context, R.drawable.baseline_close_24) as Drawable
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // do nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) showClearButton() else hideClearBtn()
            }

            override fun afterTextChanged(s: Editable) {
                //do nothing
            }

        })
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val clearBtnStart: Float
            val clearBtnEnd: Float
            var isClearBtnClicked = false
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                clearBtnEnd = (clearBtn.intrinsicWidth + paddingStart).toFloat()
                when {
                    event.x < clearBtnEnd -> isClearBtnClicked = true
                }
            } else {
                clearBtnStart = (width - paddingEnd - clearBtn.intrinsicWidth).toFloat()
                when {
                    event.x > clearBtnStart -> isClearBtnClicked = true
                }
            }
            if (isClearBtnClicked) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        clearBtn = ContextCompat.getDrawable(context, R.drawable.baseline_close_24) as Drawable
                        showClearButton()
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        clearBtn = ContextCompat.getDrawable(context, R.drawable.baseline_close_24) as Drawable
                        when {
                            text != null -> text?.clear()
                        }
                        hideClearBtn()
                        return true
                    }
                    else -> return false
                }
            } else return false
        }
        return false
    }

    private fun showClearButton() {
        setBtnDrawable(endOfTheText = clearBtn)
    }

    private fun hideClearBtn() {
        setBtnDrawable()
    }

    private fun setBtnDrawable(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    constructor(context: Context) : super (context) { init() }
    constructor(context: Context, attrs: AttributeSet): super (context, attrs) { init() }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super (context, attrs, defStyleAttr) { init() }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = context.getString(R.string.please_add_your_email)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        checkEmail()
    }

    private fun checkEmail() {
        when {
            editableText.isNotEmpty() && editableText.length < 1 -> error = context.getString(R.string.email_empty)
            !editableText.contains("@") -> error = context.getString(R.string.email_format)
        }
    }
}