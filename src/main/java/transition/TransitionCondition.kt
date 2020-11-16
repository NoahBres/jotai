package transition

fun interface TransitionCondition {
    fun shouldTransition(): Boolean
}