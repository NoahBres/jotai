package com.noahbres.jotai

import com.noahbres.jotai.transition.TransitionCondition

data class State<StateEnum>(
    var state: StateEnum,
    var enterActions: MutableList<ActionCallback>,
    var exitActions: MutableList<ActionCallback>,
    var loopActions: MutableList<ActionCallback>,
    var transitionCondition: TransitionCondition?
)