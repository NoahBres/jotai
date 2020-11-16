import transition.TransitionCondition

data class State<StateEnum>(
        var state: StateEnum,
        var enterActions: MutableList<ActionCallback>,
        var exitActions: MutableList<ActionCallback>,
        var loopActions: MutableList<ActionCallback>,
        var transitionCondition: TransitionCondition?
)