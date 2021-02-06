package com.udacity


sealed class ButtonState(var customButtonText: Int) {
    object Clicked : ButtonState(R.string.button_default)
    object Loading : ButtonState(R.string.button_loading)
    object Completed : ButtonState(R.string.button_default)
}