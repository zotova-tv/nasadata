package ru.gb.nasadata.util

import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment

fun View.show(): View {
    visibility = View.VISIBLE
    return this
}

fun View.hide(): View {
    visibility = View.GONE
    return this
}

fun Fragment.toast(string: String?) {
    Toast.makeText(context, string, Toast.LENGTH_SHORT).apply {
        setGravity(Gravity.BOTTOM, 0, 250)
        show()
    }
}