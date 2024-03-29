package com.intelliswift.errormessagelib

import android.content.Context
import android.graphics.ColorFilter
import android.graphics.PorterDuff.Mode
import android.graphics.PorterDuffColorFilter
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.LottieValueCallback
import com.intelliswift.errormessagelib.ErrorMessages.Status.ERROR
import com.intelliswift.errormessagelib.ErrorMessages.Status.HIDDEN
import com.intelliswift.errormessagelib.ErrorMessages.Status.INTERNET_ERROR
import com.intelliswift.errormessagelib.ErrorMessages.Status.LOADING
import com.intelliswift.errormessagelib.ErrorMessages.Status.MESSAGE
import com.intelliswift.errormessagelib.ErrorMessages.Status.NO_DATA
import com.intelliswift.errormessagelib.databinding.ShowMessageErrorLayoutBinding

class ErrorMessages : ConstraintLayout {
    private var _binding: ShowMessageErrorLayoutBinding? = null
    private val binding get() = _binding!!

    var typeface: Typeface? = null
        set(value) {
            field = value
            binding.tvError.typeface = value
            binding.btnAction.typeface = value
        }

    var state: State = State.hidden()
        set(value) {
            field = value
            refresh(value)
        }

    private fun refresh(state: State) {

        when (state.status) {
            LOADING -> showLoading(state)
            ERROR -> showError(state)
            INTERNET_ERROR -> showInternetError(state)
            NO_DATA -> showNoData(state)
            MESSAGE -> showMessage(state)
            HIDDEN -> hide()
        }
    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        if (isInEditMode) {
            return
        }
        _binding = ShowMessageErrorLayoutBinding.inflate(LayoutInflater.from(context), this, true)
        val root = binding.root
        root.isClickable = true
        root.isFocusable = true
    }

    private fun setTypeface(typeface: Typeface): ErrorMessages {
        binding.tvError.typeface = typeface
        binding.btnAction.typeface = typeface

        return this
    }

    private fun showMessage(state: State): ErrorMessages {
        visibility = View.VISIBLE
        hideProgressBar()
        binding.ivIcon.visibility = View.GONE
        binding.tvError.visibility = View.VISIBLE

        when (state.action) {
            null -> {
                binding.btnAction.visibility = View.GONE
            }
            else -> {
                binding.btnAction.visibility = View.VISIBLE
                binding.btnAction.setOnClickListener(state.action)
            }
        }

        binding.tvError.text = state.message
        binding.tvError.setTextColor(state.messageTextColor ?: transparent_black_percent_60)

        binding.btnAction.text = state.actionTitle ?: context.getString(R.string.i_got_it)
        if(state.actionIcon != null)
            binding.btnAction.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, state.actionIcon), null)
        binding.btnAction.setTextColor(state.actionTextColor ?: light_blue_500)

        return this
    }

    private fun hideProgressBar() {
        binding.progressBar.pauseAnimation()
        binding.progressBar.visibility = View.GONE
    }

    private fun showError(state: State): ErrorMessages {
        visibility = View.VISIBLE

        hideProgressBar()
        binding.ivIcon.visibility = View.VISIBLE
        binding.tvError.visibility = View.VISIBLE

        when (state.action) {
            null -> binding.btnAction.visibility = View.GONE
            else -> {
                binding.btnAction.visibility = View.VISIBLE
                binding.btnAction.setOnClickListener(state.action)
            }
        }

        binding.btnAction.text = state.actionTitle ?: context.getString(R.string.retry)

        val drawableCompat = ContextCompat.getDrawable(context, state.mainIcon ?: R.drawable.ic_error)
        if (drawableCompat != null) {
            binding.ivIcon.setImageDrawable(drawableCompat)
        }

        binding.tvError.text = state.message
        binding.tvError.setTextColor(state.messageTextColor ?: transparent_black_percent_60)

        binding.btnAction.text = state.actionTitle ?: context.getString(R.string.retry)
        binding.btnAction.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, state.actionIcon ?: R.drawable.ic_refresh_light_blue_500_24dp), null)
        binding.btnAction.setTextColor(state.actionTextColor ?: light_blue_500)

        return this
    }

    private fun showInternetError(state: State): ErrorMessages {
        visibility = View.VISIBLE
        hideProgressBar()
        binding.ivIcon.visibility = View.VISIBLE
        binding.tvError.visibility = View.VISIBLE

        when (state.action) {
            null -> {
                binding.btnAction.visibility = View.GONE
            }
            else -> {
                binding.btnAction.run {
                    visibility = View.VISIBLE
                    setOnClickListener(state.action)
                }
            }
        }

        val drawableCompat = ContextCompat.getDrawable(context, state.mainIcon ?: R.drawable.ic_internet_off)
        if (drawableCompat != null) {
            binding.ivIcon.setImageDrawable(drawableCompat)
        }

        binding.tvError.run {
            text = state.message ?: context.getString(R.string.no_internet_connection)
            setTextColor(state.messageTextColor ?: transparent_black_percent_60)
        }

        binding.btnAction.run {
            text = state.actionTitle ?: context.getString(R.string.retry)
            setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, state.actionIcon ?: R.drawable.ic_refresh_light_blue_500_24dp), null)
            setTextColor(state.actionTextColor ?: light_blue_500)
        }

        return this
    }

    private fun showNoData(state: State): ErrorMessages {
        visibility = View.VISIBLE

        hideProgressBar()
        binding.ivIcon.visibility = View.VISIBLE
        binding.tvError.visibility = View.VISIBLE

        when (state.action) {
            null -> {
                binding.btnAction.visibility = View.GONE
            }
            else -> {
                binding.btnAction.run {
                    visibility = View.VISIBLE
                    setOnClickListener(state.action)
                }
            }
        }

        val drawableCompat = ContextCompat.getDrawable(context, state.mainIcon ?: R.drawable.ic_sentiment_neutral_red_a100_128dp)
        if (drawableCompat != null) {
            binding.ivIcon.setImageDrawable(drawableCompat)
        }

        binding.tvError.run {
            text = state.message ?: context.getString(R.string.no_data)
            setTextColor(state.messageTextColor ?: error_text_color)
        }

        binding.btnAction.run {
            text = state.actionTitle ?: context.getString(R.string.retry)
            setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, state.actionIcon ?: R.drawable.ic_refresh_light_blue_500_24dp), null)
            setTextColor(state.actionTextColor ?: light_blue_500)
        }

        return this
    }

    private fun showLoading(state: State): ErrorMessages {
        visibility = View.VISIBLE
        val animationData: AnimationData = state.animationData ?: AnimationData(R.raw.loading, 1.0f, 0.85f, null)
        binding.progressBar.run {
            speed = animationData.speed
            scale = animationData.scale
            setAnimation(animationData.loadingRawResId)
            playAnimation()
        }

        val tintColor = animationData.tintColor
        if (tintColor != null) {
            binding.progressBar.addValueCallback<ColorFilter>(KeyPath("**"), LottieProperty.COLOR_FILTER,
                LottieValueCallback<ColorFilter>(PorterDuffColorFilter(tintColor, Mode.SRC_ATOP)))
        }

        binding.progressBar.visibility = View.VISIBLE

        val dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.wh_loading)
        binding.progressBar.layoutParams.width = dimensionPixelSize
        binding.progressBar.layoutParams.height = dimensionPixelSize
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.lConstraint)
        constraintSet.connect(R.id.progressBar, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        constraintSet.connect(R.id.progressBar, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        constraintSet.connect(R.id.progressBar, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        constraintSet.connect(R.id.progressBar, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        constraintSet.applyTo(binding.lConstraint)

        binding.ivIcon.visibility = View.GONE
        binding.tvError.visibility = View.GONE
        binding.btnAction.visibility = View.GONE

        return this
    }

    private fun showListLoading(animationData: AnimationData = AnimationData(R.raw.skeleton_loading, 12.0f, .8f, 0x36000000)): ErrorMessages {
        visibility = View.VISIBLE
        binding.progressBar.run {
            speed = animationData.speed
            scale = animationData.scale
            setAnimation(animationData.loadingRawResId)
            playAnimation()
        }

        val tintColor = animationData.tintColor
        if (tintColor != null) {
            binding.progressBar.addValueCallback<ColorFilter>(KeyPath("**"), LottieProperty.COLOR_FILTER,
                LottieValueCallback<ColorFilter>(PorterDuffColorFilter(tintColor, Mode.SRC_ATOP)))
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.progressBar.layoutParams.width = 0
        binding.progressBar.layoutParams.height = 0

        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.lConstraint)
        constraintSet.connect(R.id.progressBar, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        constraintSet.connect(R.id.progressBar, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        constraintSet.connect(R.id.progressBar, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        constraintSet.connect(R.id.progressBar, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        constraintSet.applyTo(binding.lConstraint)

        binding.ivIcon.visibility = View.GONE
        binding.tvError.visibility = View.GONE
        binding.btnAction.visibility = View.GONE
        return this
    }

    private fun hide() {
        hideProgressBar()
        visibility = View.GONE
    }

    override fun setVisibility(visibility: Int) {
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            hideProgressBar()
        }
        super.setVisibility(visibility)
    }

    fun getActionButton(): View {
        return binding.btnAction
    }

    enum class Status {
        LOADING, ERROR, INTERNET_ERROR, NO_DATA, MESSAGE, HIDDEN
    }

    data class State(
        val status: Status,
        val message: String? = null,
        @ColorInt val messageTextColor: Int? = null,
        val actionTitle: String? = null,
        @DrawableRes val mainIcon: Int? = null,
        @DrawableRes val actionIcon: Int? = null,
        @ColorInt val actionTextColor: Int? = null,
        val animationData: AnimationData? = null,
        val action: ((v: View) -> Unit)? = null,
    ) {
        companion object {
            fun loading(animationData: AnimationData = AnimationData(R.raw.loading, 12.0f, .8f, 0x36000000)): State {
                return State(
                    LOADING,
                    animationData = animationData,
                )
            }

            fun error(message: String? = null, actionTitle: String? = null, action: ((v: View) -> Unit)? = null): State = State(
                status = ERROR,
                message = message,
                actionTitle = actionTitle,
                action = action
            )

            fun internetError(
                message: String? = null,
                actionTitle: String? = null,
                action: ((v: View) -> Unit)? = null,
            ): State = State(
                status = INTERNET_ERROR,
                message = message,
                actionTitle = actionTitle,
                action = action
            )

            fun hidden(): State = State(
                status = HIDDEN,
            )

            fun noData(message: String? = null, actionTitle: String? = null, action: ((v: View) -> Unit)? = null): State = State(
                status = NO_DATA,
                message = message,
                actionTitle = actionTitle,
                action = action
            )

            fun message(message: String, actionTitle: String? = null, action: ((v: View) -> Unit)? = null): State = State(
                status = MESSAGE,
                message = message,
                actionTitle = actionTitle,
                action = action
            )
        }
    }
}
