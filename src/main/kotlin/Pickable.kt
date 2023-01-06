import GameBoard.WINDOW_HEIGHT
import GameBoard.WINDOW_WIDTH
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage

abstract class Pickable(val pos: Vector, val color: Color, val sprite: BufferedImage? = null) {
    open val size = 1
    fun draw(heroX: Int, heroY: Int, g: Graphics2D) {
        g.color = color
        g.fillOval(pos.x.toInt() - heroX + WINDOW_WIDTH / 2 - size / 2, pos.y.toInt() - heroY + WINDOW_HEIGHT / 2 - size / 2, size, size)
        g.color = Color.black
    }

    open fun goTowardHero(hero: Hero) {

    }
}

class MoneyPickable(pos: Vector, color: Color = Color.orange) : Pickable(pos, color) {
    override val size = 10
}

class ExperiencePickable(pos: Vector, amount: Int = 1, color: Color = Color.green) :
    Pickable(pos, color) {
    override val size = 6
}

class FoodPickable(pos: Vector, color: Color = Color.DARK_GRAY, val regen: Int = 10) :
    Pickable(pos, color) {
    override val size = 12
}