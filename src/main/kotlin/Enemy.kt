import GameBoard.WINDOW_HEIGHT
import GameBoard.WINDOW_WIDTH
import java.awt.Color
import java.awt.Graphics
import java.awt.image.BufferedImage

abstract class Enemy(
    val pos: Vector,
    var health: Int,
    var damage: Int,
    var speed: Double,
    var size: Int,
    val sprite: BufferedImage,
    val attackInterval: Double
) {
    fun moveToHero(hero: Hero) {
        val vel = hero.pos.copy()
        vel.sub(pos)
        print("${vel.x}, ${vel.y}")
        vel.limit(4.5)
        println(" === ${vel.x}, ${vel.y}")
        pos.add(vel)
    }

    fun attack() {
        //    TODO
    }

    open fun hurt(damage: Int) {
        //    TODO))
    }

    open fun draw(graphics: Graphics, hero: Hero) {
        graphics.fillOval(
            pos.x - hero.pos.x + WINDOW_WIDTH / 2 - size / 2,
            pos.y - hero.pos.y + WINDOW_HEIGHT / 2 - size / 2,
            size,
            size
        )
    }
}

class BossEnemy(
    pos: Vector,
    health: Int,
    damage: Int,
    speed: Double,
    size: Int,
    sprite: BufferedImage,
    attackInterval: Double
) : Enemy(pos, damage, health, speed, size, sprite, attackInterval)


class BaseEnemy(
    pos: Vector,
    health: Int,
    damage: Int,
    speed: Double,
    size: Int,
    sprite: BufferedImage,
    attackInterval: Double,
    val color: Color
) : Enemy(pos, damage, health, speed, size, sprite, attackInterval) {
    override fun draw(graphics: Graphics, hero: Hero) {
        graphics.color = color
        super.draw(graphics, hero)
        graphics.color = Color.BLACK
    }
}