import GameBoard.WINDOW_HEIGHT
import GameBoard.WINDOW_WIDTH
import java.awt.Color
import java.awt.Graphics
import java.awt.image.BufferedImage
import kotlin.random.Random

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
        val force = hero.pos.copy()
//        force.x += Random.nextInt(-50, 50)
//        force.y += Random.nextInt(-50, 50)
        force.sub(pos)
        force.setMag(speed / 2)
        if (pos.dist(hero.pos) <= hero.size / 1.5 + size / 2) {
            force.setMag(0.0)
        }
        pos.add(force)
    }

    fun manageCollisions(enemies: List<Enemy>) {
        return
        val vel = Vector(0, 0)
        enemies.forEach { en ->
            if (isCollidingWithEnemy(en)) {
                val force = pos.copy()
                force.sub(en.pos)
                force.normalize()
                vel.add(force)
            }
        }
        pos.add(vel)
    }

    fun isCollidingWithEnemy(enemy: Enemy) : Boolean {
        return pos.dist(enemy.pos) <= size / 1.5 + enemy.size / 2
    }

    fun attack() {
        //    TODO
    }

    open fun hurt(damage: Int) {
        //    TODO
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