import net.java.games.input.Component.POV
import net.java.games.input.Component.Identifier
import net.java.games.input.Component.Identifier.Axis
import net.java.games.input.Component.Identifier.Button
import net.java.games.input.Controller
import net.java.games.input.ControllerEnvironment

data class GamepadHat(
    var top: Boolean = false,
    var right: Boolean = false,
    var bottom: Boolean = false,
    var left: Boolean = false
)

data class GamepadStick(
    var x: Double = 0.0,
    var y: Double = 0.0
) {
    override fun toString(): String = "($x, $y)"
}

data class GamepadButtons(
    var X: Boolean = false,
    var Y: Boolean = false,
    var A: Boolean = false,
    var B: Boolean = false,
    var START: Boolean = false,
    var SELECT: Boolean = false,
    var LT: Boolean = false,
    var RT: Boolean = false,
    var LB: Boolean = false,
    var RB: Boolean = false
)

data class GamepadState(
    val hat: GamepadHat = GamepadHat(),
    val leftStick: GamepadStick = GamepadStick(),
    val rightStick: GamepadStick = GamepadStick(),
    val buttons: GamepadButtons = GamepadButtons()
) {
    fun copy(): GamepadState {
        return GamepadState(hat.copy(), leftStick.copy(), rightStick.copy(), buttons.copy())
    }
}

class Gamepad {
    var controller: Controller? = null

    private var STICK_TRESHOLD: Double = 0.25

    val state: GamepadState = GamepadState()
    var previousState: GamepadState = state

    fun init() {
        val controllers = ControllerEnvironment.getDefaultEnvironment().controllers
        controller = controllers.firstOrNull { it.type == Controller.Type.GAMEPAD }
    }

    private fun getStickValueWithinTreshold(id: Identifier): Double {
        val value = controller?.getComponent(id)?.pollData?.toDouble() ?: return 0.0
        if (STICK_TRESHOLD <= value || value <= -STICK_TRESHOLD) return value
        return 0.0
    }

    fun update() {
        controller?.poll()

        state.leftStick.x = getStickValueWithinTreshold(Axis.X)
        state.leftStick.y = getStickValueWithinTreshold(Axis.Y)

        state.rightStick.x = getStickValueWithinTreshold(Axis.RX)
        state.rightStick.y = getStickValueWithinTreshold(Axis.RY)

        //// HAT
        val hatValue = controller?.getComponent(Axis.POV)?.pollData
        state.hat.top =
            hatValue == POV.UP || hatValue == POV.UP_LEFT || hatValue == POV.UP_RIGHT
        state.hat.bottom =
            hatValue == POV.DOWN || hatValue == POV.DOWN_LEFT || hatValue == POV.DOWN_RIGHT
        state.hat.left =
            hatValue == POV.LEFT || hatValue == POV.DOWN_LEFT || hatValue == POV.UP_LEFT
        state.hat.right =
            hatValue == POV.RIGHT || hatValue == POV.DOWN_RIGHT || hatValue == POV.UP_RIGHT

        //// BUTTONS
        state.buttons.A = controller?.getComponent(Button._0)?.pollData!! > 0
        state.buttons.B = controller?.getComponent(Button._1)?.pollData!! > 0
        state.buttons.X = controller?.getComponent(Button._2)?.pollData!! > 0
        state.buttons.Y = controller?.getComponent(Button._3)?.pollData!! > 0
        state.buttons.LB = controller?.getComponent(Button._4)?.pollData!! > 0
        state.buttons.RB = controller?.getComponent(Button._5)?.pollData!! > 0
        state.buttons.SELECT = controller?.getComponent(Button._6)?.pollData!! > 0
        state.buttons.START = controller?.getComponent(Button._7)?.pollData!! > 0
        state.buttons.LT = controller?.getComponent(Axis.Z)?.pollData!! > 0.2
        state.buttons.RT = controller?.getComponent(Axis.Z)?.pollData!! < 0.2

        // println("previous: ${previousState.buttons.X} - new: ${state.buttons.X}")
        if (previousState.buttons.X != state.buttons.X) println("X: ${state.buttons.X}")


        previousState = state.copy()
    }
}