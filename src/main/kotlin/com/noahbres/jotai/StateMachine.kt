package com.noahbres.jotai

import com.noahbres.jotai.transition.TimedTransition
import com.noahbres.jotai.transition.WaitOnStartTransition

class StateMachine<StateEnum>(private val stateList: List<State<StateEnum>>, private val exitToState: StateEnum?) {
    var running = false
        private set

    var looping = false

    private var currentState = stateList.first()

    fun start() {
        running = true

        stateList.forEach {
            if (it.transitionCondition is WaitOnStartTransition)
                (it.transitionCondition as WaitOnStartTransition).hasStarted = true

            if (currentState.transitionCondition is TimedTransition)
                (currentState.transitionCondition as TimedTransition).startTimer()

            currentState.enterActions.forEach { me -> me.run() }
        }
    }

    fun stop() {
        running = false
    }

    fun reset() {
        currentState = stateList.first()

        stateList.forEach {
            if (it.transitionCondition is WaitOnStartTransition)
                (it.transitionCondition as WaitOnStartTransition).hasStarted = false
        }
    }

    fun getState(): StateEnum {
        return currentState.state
    }

    fun update() {
        if (!running) return

        if (currentState.transitionCondition?.shouldTransition() == true)
            transition()

        currentState.loopActions.forEach { it.run() }
    }

    // returns true if change in state
    // returns false if not
    fun transition(): Boolean {
        currentState.exitActions.forEach { it.run() }

        if (stateList.last() == currentState) {
            if (!looping)
                running = false

            // Reset WaitOnStartTransitions
            if (!looping) {
                stateList.forEach {
                    if (it.transitionCondition is WaitOnStartTransition)
                        (it.transitionCondition as WaitOnStartTransition).hasStarted = false
                }
            }

            // If we want to exit to a certain state, set currentState to that state
            // Otherwise just return false and don't transition
            if (exitToState != null) {
                val searchForState = stateList.find { it.state == exitToState }

                if (searchForState != null) currentState = searchForState else return false
            } else {
                return false
            }
        } else {
            currentState = stateList[stateList.indexOf(currentState) + 1]
        }

        currentState.enterActions.forEach { it.run() }

        if (currentState.transitionCondition is TimedTransition)
            (currentState.transitionCondition as TimedTransition).startTimer()

        return true
    }
}