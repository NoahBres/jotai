package com.noahbres.jotai.transition

class WaitOnStartTransition: TransitionCondition {
    var hasStarted = false

    override fun shouldTransition(): Boolean {
        return hasStarted
    }
}