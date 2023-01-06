import GameBoard.WINDOW_HEIGHT
import GameBoard.WINDOW_WIDTH
import java.awt.Color
import java.awt.Graphics
import java.awt.geom.AffineTransform
import java.awt.geom.AffineTransform.*
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.random.Random

enum class EnemyState {
    IDLE,
    HURT,
    ATTACK
}

abstract class Enemy(
    val pos: Vector,
    var health: Int,
    var damage: Int,
    var speed: Double,
    var size: Int,
    var sprite: BufferedImage,
    val attackInterval: Double
) {
    var toRemoveNextFrame: Boolean = false
    var state: EnemyState = EnemyState.IDLE

    init {
//        println("sprite before: (${sprite.height}, ${sprite.width})")
//        val scale = (size / sprite.width).toDouble()
//        val transform = getScaleInstance(scale, scale)
//        val op = AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR)
//        sprite = op.filter(sprite, null)
//        println("sprite after: (${sprite.height}, ${sprite.width})")

    }

    fun moveToHero(hero: Hero) {
        val force = hero.pos.copy()
//        force.x += Random.nextInt(-80, 80)
//        force.y += Random.nextInt(-80, 80)
        force.sub(pos)
        force.setMag(speed / 2)
        if (pos.dist(hero.pos) <= hero.size / 1.5 + size / 2) {
            force.setMag(0.0)
        }
        pos.add(force)
    }

    fun manageCollisions(enemies: List<Enemy>) {
        val vel = Vector(0.0, 0.0)
        enemies.forEach { en ->
            if (isCollidingWithEnemy(en)) {
                val force = pos.copy()
                force.sub(en.pos)
                force.setMag(speed / 2)
                vel.add(force)
            }
        }
        pos.add(vel)
    }

    fun isCollidingWithEnemy(enemy: Enemy): Boolean {
        return pos.dist(enemy.pos) <= size / 1.5 + enemy.size / 2
    }

    fun attack() {
        //    TODO
    }

    open fun hurt(damage: Int) {
        health -= damage
//        println("health: $health")
        state = EnemyState.HURT
        toRemoveNextFrame = health <= 0
    }

    open fun draw(graphics: Graphics, hero: Hero) {
//        println(health)
//        graphics.fillOval(
//            (pos.x - ceil(hero.pos.x) + WINDOW_WIDTH / 2 - size / 2).toInt(),
//            (pos.y - ceil(hero.pos.y) + WINDOW_HEIGHT / 2 - size / 2).toInt(),
//            size,
//            size
//        )
        graphics.drawImage(
            sprite,
            (pos.x - ceil(hero.pos.x) + WINDOW_WIDTH / 2 - sprite.width / 2).toInt(),
            (pos.y - ceil(hero.pos.y) + WINDOW_HEIGHT / 2 - sprite.height / 2).toInt(),
            null
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
) : Enemy(pos, health, damage, speed, size, sprite, attackInterval) {

    val idleSprite = ImageIO.read(File("src/main/resources/amogus_32.png"))
    val hurtSprite: BufferedImage = ImageIO.read(File("src/main/resources/amogus_hurt_32.png"))

    override fun draw(graphics: Graphics, hero: Hero) {
        sprite = when (state) {
            EnemyState.IDLE -> idleSprite
            EnemyState.HURT -> hurtSprite
            else -> idleSprite
        }
        if (state == EnemyState.HURT) println("HOLALALA JSUIS HURT OUILLE OUILLE")
        super.draw(graphics, hero)
        state = EnemyState.IDLE
    }
}