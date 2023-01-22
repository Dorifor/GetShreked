package GUI

import GUIElement
import java.awt.Point

interface Clickable {
    var onClick: (() -> Unit)?
}

enum class ClickableState {
    NORMAL,
    FOCUSED,
    CLICKED
}

abstract class ClickableElement(
    val pos: Point
): GUIElement, Clickable {
    var state = ClickableState.NORMAL
    var selected = false
    override var onClick: (() -> Unit)? = null
}
