package com.noahbres.jotai.dsl

import com.noahbres.jotai.ActionCallback
import com.noahbres.jotai.State
import com.noahbres.jotai.StateMachine
import com.noahbres.jotai.transition.TimedTransition
import com.noahbres.jotai.transition.TransitionCondition
import com.noahbres.jotai.transition.WaitOnStartTransition

@StateMachineDSL
class BuilderDSL<StateEnum>(private val stateList: MutableList<State<StateEnum>> = mutableListOf()) {
    private val existingStates: MutableList<StateEnum> = mutableListOf()

    var exitState: StateEnum? = null

    operator fun State<StateEnum>.unaryPlus() {
        require(this.state !in existingStates) { "State already exists in the state machine" }

        existingStates.add(this.state)

        stateList.add(this)
    }

    fun state(state: StateEnum, block: StateContext<StateEnum>.() -> Unit = {}): State<StateEnum> =
            StateContext<StateEnum>(state).apply(block).currentState

    fun exit(exitToState: StateEnum) {
        exitState = exitToState
    }

    fun build(): StateMachine<StateEnum> = StateMachine(stateList, exitState)
}

fun <E> stateMachine(block: BuilderDSL<E>.() -> Unit): StateMachine<E> =
        BuilderDSL<E>().apply(block).build()

fun <E> stateMachine(startingStates: List<State<E>>, block: BuilderDSL<E>.() -> Unit): StateMachine<E> =
        BuilderDSL(startingStates.toMutableList()).apply(block).build()

@StateMachineDSL
class StateContext<StateEnum>(state: StateEnum) {
    val currentState = State(state, mutableListOf(), mutableListOf(), mutableListOf(), null)

    fun transition(transitionCondition: TransitionCondition): StateContext<StateEnum> = this.apply {
        currentState.transitionCondition = transitionCondition
    }

    fun transitionTimed(time: Number): StateContext<StateEnum> = this.apply {
        transition(TimedTransition(time.toDouble()))
    }

    fun waitForStart(): StateContext<StateEnum> = this.apply {
        transition(WaitOnStartTransition())
    }

    fun onEnter(callback: ActionCallback): StateContext<StateEnum> = this.apply {
        currentState.enterActions.add(callback)
    }

    fun onExit(callback: ActionCallback): StateContext<StateEnum> = this.apply {
        currentState.exitActions.add(callback)
    }

    fun onLoop(callback: ActionCallback): StateContext<StateEnum> = this.apply {
        currentState.loopActions.add(callback)
    }
}