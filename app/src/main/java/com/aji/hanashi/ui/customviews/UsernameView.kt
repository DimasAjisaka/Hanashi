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

class UsernameView : AppCompatEditText, View.OnTouchListener {
    private lateinit var clearBtn: Drawable

    private fun init() {
        clearBtn = ContextCompat.getDrawable(context, R.drawable.baseline_close_24) as Drawable
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                //do nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) showClearButton() else hideClearButton()
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
            var isClearBtnCliked = false
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                clearBtnEnd = (clearBtn.intrinsicWidth + paddingStart).toFloat()
                when {
                    event.x < clearBtnEnd -> isClearBtnCliked = true
                }
            } else {
                clearBtnStart = (width - paddingEnd - clearBtn.intrinsicWidth).toFloat()
                when {
                    event.x > clearBtnStart -> isClearBtnCliked = true
                }
            }
            if (isClearBtnCliked) {
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
                        hideClearButton()
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

    private fun hideClearButton() {
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
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { init() }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { init() }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = context.getString(R.string.please_add_your_username)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        checkUsername()
    }

    private fun checkUsername() {
        when {
            editableText.isNotEmpty() && editableText.isEmpty() -> error = context.getString(R.string.username_empty)
        }
    }
}