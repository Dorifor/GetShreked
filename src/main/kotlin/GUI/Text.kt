package GUI

import GUIElement
import Hero
import java.awt.Color
import java.awt.Graphics
import java.awt.Point

abstract class Text(
    val pos: Point,
    val text: String
) : GUIElement {
    protected var displayText = ""

    override fun draw(graphics: Graphics) {
        graphics.color = Color.WHITE
        val height = graphics.fontMetrics.height
        graphics.drawString(displayText, pos.x, pos.y + height)
        graphics.color = Color.BLACK
    }
}

class LevelText(pos: Point, text: String) : Text(pos, text) {
    var hero: Hero? = null

    override fun update() {
        displayText = text.replace("%s%", hero?.level.toString())
    }
}

class CoinsText(pos: Point, text: String) : Text(pos, text) {
    var hero: Hero? = null

    override fun draw(graphics: Graphics) {
        graphics.color = Color.WHITE
        val textHeight = graphics.fontMetrics.height
        val textWidth = graphics.fontMetrics.stringWidth(displayText)
        graphics.drawString(displayText, pos.x - textWidth, pos.y + textHeight)
        graphics.color = Color.BLACK
    }

    override fun update() {
        displayText = text.replace("%s%", hero?.coins.toString())
    }
}
