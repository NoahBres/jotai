package transition

import transition.TransitionCondition

class WaitOnStartTransition: TransitionCondition {
    var hasStarted = false

    override fun shouldTransition(): Boolean {
        return hasStarted
    }
}