import com.noahbres.jotai.StateMachine
import com.noahbres.jotai.StateMachineBuilder
import org.junit.jupiter.api.Test
import java.lang.Thread.sleep

internal class StateMachineTest {
    companion object {
        var conditionTest = false
    }

    @Test
    fun stateMachine_initTest() {
        val stateMachine = buildStateMachine()

        assert(stateMachine.getState() == MyStates.IDLE)
    }

    @Test
    fun stateMachine_transitionTest() {
        val stateMachine = buildStateMachine()

        // Check initial state
        assert(stateMachine.getState() == MyStates.IDLE)

        // Check waitForStart()
        stateMachine.start()

        stateMachine.update()

        assert(stateMachine.getState() == MyStates.STATE_1)

        // Check the transition
        stateMachine.update()

        // com.noahbres.jotai.State shouldnt transition yet
        assert(stateMachine.getState() == MyStates.STATE_1)

        conditionTest = true

        stateMachine.update()

        // com.noahbres.jotai.State should update after conditionTest = true
        assert(stateMachine.getState() == MyStates.STATE_2)

        // Check that the state1 on exit is called
        assert(!conditionTest)

        sleep(200)

        stateMachine.update()

        // com.noahbres.jotai.State shouldnt transition yet because only 1 second passed
        assert(stateMachine.getState() == MyStates.STATE_2)

        sleep(200)

        stateMachine.update()

        // com.noahbres.jotai.State should transition because 2.1 seconds has passed
        assert(stateMachine.getState() == MyStates.STATE_3)

        stateMachine.update()

        // com.noahbres.jotai.State shouldn't update because it has no transition
        assert(stateMachine.getState() == MyStates.STATE_3)

        // Force transition
        stateMachine.transition()

        stateMachine.update()

        assert(stateMachine.getState() == MyStates.IDLE)

        // com.noahbres.jotai.State machine reached exit condition
        assert(!stateMachine.running)
    }

    @Test
    fun stateMachine_transitionNoExitTest() {
        val stateMachine = StateMachineBuilder<MyStates>()
                .state(MyStates.IDLE)
                .waitForStart()

                .state(MyStates.STATE_1)
                .transition { true }

                .state(MyStates.STATE_2)
                .transition { true }

                .build()

        stateMachine.start()

        stateMachine.update()

        assert(stateMachine.getState() == MyStates.STATE_1)

        stateMachine.update()

        assert(stateMachine.getState() == MyStates.STATE_2)

        stateMachine.update()

        assert(stateMachine.getState() == MyStates.STATE_2)
    }

    @Test
    fun stateMachine_noWaitForStart() {
        val stateMachine = StateMachineBuilder<MyStates>()
                .state(MyStates.IDLE)
                .transition { true }

                .state(MyStates.STATE_1)
                .transition { true }

                .state(MyStates.STATE_2)

                .build()

        stateMachine.start()

        stateMachine.update()
        stateMachine.update()

        assert(stateMachine.getState() == MyStates.STATE_2)
    }

    private fun buildStateMachine(): StateMachine<MyStates> {
        return StateMachineBuilder<MyStates>()
                .state(MyStates.IDLE)
                .waitForStart()
                .state(MyStates.STATE_1)
                .onEnter {
                    conditionTest = false
                }
                .transition { conditionTest }
                .onExit { conditionTest = false }

                .state(MyStates.STATE_2)
                .transitionTimed(0.3)

                .state(MyStates.STATE_3)

                .exit(MyStates.IDLE)

                .build()
    }

    internal enum class MyStates {
        IDLE,
        STATE_1,
        STATE_2,
        STATE_3,
    }
}