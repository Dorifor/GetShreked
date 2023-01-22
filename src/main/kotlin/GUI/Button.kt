package GUI

import GameBoard.mouseClicked
import GameBoard.mouseReleased
import java.awt.Color
import java.awt.Graphics
import java.awt.Point

class Button(
    pos: Point,
    val text: String,
    val width: Int,
    val height: Int,
    val textColor: Color = Color.BLACK,
    val backgroundColor: Color = Color.GRAY,
    val focusedTextColor: Color = Color.WHITE,
    val focusedBackgroundColor: Color = Color.DARK_GRAY,
    val clickedTextColor: Color = Color.red,
    val clickedBackgroundColor: Color = Color.LIGHT_GRAY
) : ClickableElement(pos) {
    override fun update() {
        if (state == ClickableState.CLICKED && mouseReleased) {
            onClick?.let { it() }
            GameBoard.mouseReleased = false
        }

        state = if (isHovered() || selected) ClickableState.FOCUSED else ClickableState.NORMAL

        if (state == ClickableState.FOCUSED && mouseClicked) {
            state = ClickableState.CLICKED
        }
    }

    fun isHovered(): Boolean {
        return (GameBoard.mousePos.x >= pos.x - width / 2 && GameBoard.mousePos.x <= pos.x + width - width / 2) &&
                (GameBoard.mousePos.y >= pos.y - height / 2 && GameBoard.mousePos.y <= pos.y + height - height / 2)
    }

    override fun draw(graphics: Graphics) {
        graphics.color = when (state) {
            ClickableState.NORMAL -> backgroundColor
            ClickableState.FOCUSED -> focusedBackgroundColor
            ClickableState.CLICKED -> clickedBackgroundColor
        }
        graphics.fillRect(pos.x - width / 2, pos.y - height / 2, width, height)

        graphics.color = when (state) {
            ClickableState.NORMAL -> textColor
            ClickableState.FOCUSED -> focusedTextColor
            ClickableState.CLICKED -> clickedTextColor
        }
        // Center a string horizontally and vertically
        val stringWidth = graphics.fontMetrics.stringWidth(text)
        val stringHeight = graphics.fontMetrics.height
        graphics.drawString(text, pos.x - stringWidth / 2, pos.y + stringHeight / 3)
    }
}