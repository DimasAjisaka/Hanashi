package com.aji.hanashi.ui.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.aji.hanashi.R

class PassView : AppCompatEditText, View.OnTouchListener {
    private lateinit var showBtn: Drawable

    private fun init() {
        showBtn = ContextCompat.getDrawable(context, R.drawable.baseline_visibility_24) as Drawable
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
            var isClearBtnClicked = false
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                clearBtnEnd = (showBtn.intrinsicWidth + paddingStart).toFloat()
                when {
                    event.x < clearBtnEnd -> isClearBtnClicked = true
                }
            } else {
                clearBtnStart = (width - paddingEnd - showBtn.intrinsicWidth).toFloat()
                when {
                    event.x > clearBtnStart -> isClearBtnClicked = true
                }
            }
            if (isClearBtnClicked) {
                return when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        showBtn = ContextCompat.getDrawable(context, R.drawable.baseline_visibility_24) as Drawable
                        showClearButton()
                        true
                    }

                    MotionEvent.ACTION_UP -> {
                        showBtn = ContextCompat.getDrawable(context, R.drawable.baseline_visibility_24) as Drawable
                        when {
                            text != null -> showText()
                        }
                        true
                    }

                    else -> false
                }
            } else return false
        }
        return false
    }

    private fun showClearButton() {
        setBtnDrawable(endOfTheText = showBtn)
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

    constructor(context: Context): super (context) { init() }
    constructor(context: Context, attrs: AttributeSet): super (context, attrs) { init() }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super (context, attrs, defStyleAttr) { init() }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = context.getString(R.string.please_enter_your_password)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        checkPass()
    }

    private fun checkPass() {
        when {
            editableText.length < 8 && editableText.isNotEmpty() -> error = context.getString(R.string.password_wrong)
        }
    }

   private fun showText() {
       transformationMethod = if (transformationMethod == PasswordTransformationMethod.getInstance()) {
           HideReturnsTransformationMethod.getInstance()
       } else {
           PasswordTransformationMethod.getInstance()
       }
   }
}