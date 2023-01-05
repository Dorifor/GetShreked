import GameBoard.WINDOW_HEIGHT
import GameBoard.WINDOW_WIDTH
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage

abstract class Pickable(val posX: Int, val posY: Int, val color: Color, val sprite: BufferedImage? = null) {
    open val size = 1
    fun draw(heroX: Int, heroY: Int, g: Graphics2D) {
        g.color = color
        g.fillOval(posX - heroX + WINDOW_WIDTH / 2 - size / 2, posY - heroY + WINDOW_HEIGHT / 2 - size / 2, size, size)
        g.color = Color.black
    }
}

class MoneyPickable(posX: Int, posY: Int, color: Color = Color.orange) : Pickable(posX, posY, color) {
    override val size = 10
}

class ExperiencePickable(posX: Int, posY: Int, amount: Int = 1, color: Color = Color.green) :
    Pickable(posX, posY, color) {
    override val size = 6
}

class FoodPickable(posX: Int, posY: Int, color: Color = Color.DARK_GRAY, val regen: Int = 10) :
    Pickable(posX, posY, color) {
    override val size = 12
}