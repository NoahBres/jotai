import transition.TransitionCondition

data class State<StateEnum>(
        var state: StateEnum,
        var enterActions: MutableList<ActionCallback> = mutableListOf(),
        var exitActions: MutableList<ActionCallback> = mutableListOf(),
        var loopActions: MutableList<ActionCallback> = mutableListOf(),
        var transitionCondition: TransitionCondition? = null
)