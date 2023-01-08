import GameBoard.convert
import java.awt.Color
import java.awt.Graphics
import java.awt.Point

interface GUIElement {
    fun update()
    fun draw(graphics: Graphics)
}

abstract class Bar(
    val pos: Point,
    private val width: Int,
    private val height: Int,
    var value: Int = 0,
    var valueMax: Int = 100,
    private val border: Int = 2,
    private val color: Color = Color.green,
) : GUIElement {
    override fun draw(graphics: Graphics) {
        // Bar Border
        // graphics.color = Color.WHITE
        graphics.fillRect(pos.x, pos.y, width, height)

        // Bar Background
        graphics.color = Color.DARK_GRAY
        graphics.fillRect(
            pos.x + border,
            pos.y + border,
            width - 2 * border,
            height - 2 * border
        )

        // Scale value to bar width (minus border)
        val innerBarWidth = (0..valueMax).convert(value, 0..(width - 2 * border)).coerceIn(0..(width - 2 * border))

        // Inner Bar
        graphics.color = color
        graphics.fillRect(
            pos.x + border,
            pos.y + border,
            innerBarWidth,
            height - 2 * border
        )
        // Reset color to black
        graphics.color = Color.BLACK
    }
}

class HealthBar(
    pos: Point,
    width: Int,
    height: Int,
    value: Int = 0,
    valueMax: Int = 100,
    border: Int = 2,
    color: Color = Color(224, 27, 9)
) : Bar(
    pos, width, height, value,
    valueMax, border, color
) {
    var hero: Hero? = null

    override fun update() {
        value = hero?.health ?: 0
    }
}

class ExperienceBar(
    pos: Point,
    width: Int,
    height: Int,
    value: Int = 0,
    valueMax: Int = 30,
    border: Int = 2,
    color: Color = Color(0, 128, 0)
) : Bar(
    pos, width, height, value,
    valueMax, border, color
) {
    var hero: Hero? = null

    override fun update() {
        value = hero?.experience ?: 0
    }
}