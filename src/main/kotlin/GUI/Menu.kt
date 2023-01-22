package GUI

import GUIElement
import GameBoard.WINDOW_HEIGHT
import GameBoard.WINDOW_WIDTH
import GameBoard.creamyGardenFontResource
import java.awt.Color
import java.awt.Graphics
import java.awt.Point
import javax.swing.JLabel


class Menu : GUIElement {
    val buttons: MutableList<Button> = mutableListOf()
    var gap = 16
    var margin = 35
    var innerMargin = 16

    override fun update() {
        buttons.forEach { it.update() }
    }

    override fun draw(graphics: Graphics) {
        graphics.font = creamyGardenFontResource.deriveFont(32f)
        drawBackground(graphics)
        buttons.forEach { it.draw(graphics) }
    }

    fun generate(elements: Map<String, (() -> Unit)>) {
        buttons.clear()
        val largestText = elements.keys.maxBy { it.length }
        val jlabel = JLabel(largestText)
        jlabel.font = creamyGardenFontResource.deriveFont(32f)
        val largestTextSize = jlabel.preferredSize

        elements.entries.forEachIndexed { idx, (text, callback) ->
            val buttonHeight = largestTextSize.height + innerMargin
            val buttonWidth = largestTextSize.width + 2 * innerMargin

            val totalHeight = elements.size * buttonHeight + (elements.size - 1) * gap
            val posY = idx * (buttonHeight + gap) - totalHeight / 2 + buttonHeight / 2
            val button = Button(Point(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2 + posY), text, buttonWidth, buttonHeight)
            button.onClick = callback
            buttons.add(button)
        }
    }

    private fun drawBackground(graphics: Graphics) {
        val height = buttons.sumOf { it.height } + (buttons.size - 1) * gap + 2 * margin
        val width = (buttons[0].width + 2 * (2.0 / 3) * margin).toInt()

        // println("width: $width, height: $height")

        graphics.color = Color(0, 0, 0, 63)
        graphics.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT)
        graphics.color = Color(0, 0, 0, 192)
        graphics.fillRect(WINDOW_WIDTH / 2 - width / 2, WINDOW_HEIGHT / 2 - height / 2, width, height)
    }
}