package ru.gb.nasadata.util

import android.view.View

fun View.show(): View {
    visibility = View.VISIBLE
    return this
}

fun View.hide(): View {
    visibility = View.GONE
    return this
}