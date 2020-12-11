package com.noahbres.jotai.transition

fun interface TransitionCondition {
    fun shouldTransition(): Boolean
}