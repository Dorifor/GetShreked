import GameBoard.WINDOW_HEIGHT
import GameBoard.WINDOW_WIDTH
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage

abstract class Pickable(var pos: Vector, val color: Color, val sprite: BufferedImage? = null) {
    open val size = 1
    fun draw(heroX: Int, heroY: Int, g: Graphics2D) {
        g.color = color
        g.fillOval(pos.x.toInt() - heroX + WINDOW_WIDTH / 2 - size / 2, pos.y.toInt() - heroY + WINDOW_HEIGHT / 2 - size / 2, size, size)
        g.color = Color.black
    }

    open fun goTowardHero(hero: Hero) {
        if (pos.distance(hero.pos) <= hero.attractDistance) {
            var force = hero.pos - pos
            force.normalize()
            force *= hero.speed * 1.2
            pos += force
        }
    }
}

class MoneyPickable(pos: Vector, color: Color = Color.orange) : Pickable(pos, color) {
    override val size = 20
}

class ExperiencePickable(pos: Vector, amount: Int = 1, color: Color = Color.green) :
    Pickable(pos, color) {
    override val size = 10
}

class FoodPickable(pos: Vector, color: Color = Color(139, 69, 19), val regen: Int = 5) :
    Pickable(pos, color) {
    override val size = 15
}