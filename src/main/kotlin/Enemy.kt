import GameBoard.FRAMES_PER_SEC
import GameBoard.WINDOW_HEIGHT
import GameBoard.WINDOW_WIDTH
import java.awt.Color
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.floor

enum class EnemyState {
    IDLE,
    HURT,
    ATTACK
}

abstract class Enemy(
    var pos: Vector,
    var health: Int,
    var damage: Int,
    var speed: Double,
    var size: Double,
    var sprite: BufferedImage,
    val attackInterval: Double
) {
    var toRemoveNextFrame: Boolean = false
    var state: EnemyState = EnemyState.IDLE
    var force: Vector = Vector(0.0, 0.0)
    var vel: Vector = Vector(0.0, 0.0)
    var frame = 0

    init {
        // val scale = size / sprite.width
        // val transform = getScaleInstance(scale, scale)
        // val op = AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR)
        // sprite = op.filter(sprite, null)
    }

    fun moveToHero(hero: Hero) {
        force = Vector(hero.pos)
        // force.x += Random.nextInt(-80, 80)
        // force.y += Random.nextInt(-80, 80)
        force -= pos
        force.normalize()
        force *= 3.0
        frame++

        if (pos.distance(hero.pos) <= hero.size / 2 + size / 2) {
            force *= 0.0

            if (state == EnemyState.IDLE && frame >= FRAMES_PER_SEC / 2) {
                frame = 0
                attack(hero)
            }
        }

        pos += force
    }

    fun manageCollisions(enemies: List<Enemy>) {
        var vel = Vector(0.0, 0.0)
        enemies.forEach { en ->
            if (isCollidingWithEnemy(en)) {
                var force = Vector(pos)
                force -= en.pos
                force.normalize()
                force *= speed / 2
                vel += force
            }
        }
        pos += vel
    }

    fun isCollidingWithEnemy(enemy: Enemy): Boolean {
        return pos.distance(enemy.pos) <= size / 1.5 + enemy.size
    }

    fun attack(hero: Hero) {
        state = EnemyState.ATTACK
        hero.hurt(damage)
        state = EnemyState.IDLE
    }

    open fun hurt(damage: Int) {
        health -= damage
        state = EnemyState.HURT
        toRemoveNextFrame = health <= 0
    }

    open fun draw(graphics: Graphics, hero: Hero) {
        graphics.drawImage(
            sprite,
            (pos.x - floor(hero.pos.x) + WINDOW_WIDTH / 2 - sprite.width / 2).toInt(),
            (pos.y - floor(hero.pos.y) + WINDOW_HEIGHT / 2 - sprite.height / 2).toInt(),
            null
        )

        //// DEBUG PURPOSE
        // graphics.fillRect(
        //     (pos.x - floor(hero.pos.x) + WINDOW_WIDTH / 2 - sprite.width / 2).toInt(),
        //     (pos.y - floor(hero.pos.y) + WINDOW_HEIGHT / 2 - sprite.height / 2).toInt(),
        //     size.toInt(),
        //     size.toInt()
        // )
    }
}

class BossEnemy(
    pos: Vector,
    health: Int,
    damage: Int,
    speed: Double,
    size: Double,
    sprite: BufferedImage,
    attackInterval: Double
) : Enemy(pos, damage, health, speed, size, sprite, attackInterval)


class BaseEnemy(
    pos: Vector,
    health: Int,
    damage: Int,
    speed: Double,
    size: Double,
    sprite: BufferedImage,
    attackInterval: Double,
    var color: Color
) : Enemy(pos, health, damage, speed, size, sprite, attackInterval) {

    val idleSprite = ImageIO.read(File("src/main/resources/amogus_64.png"))
    val hurtSprite: BufferedImage = ImageIO.read(File("src/main/resources/amogus_hurt_64.png"))

    override fun draw(graphics: Graphics, hero: Hero) {
        graphics.color = color
        sprite = when (state) {
            EnemyState.IDLE -> idleSprite
            EnemyState.HURT -> hurtSprite
            else -> idleSprite
        }
        super.draw(graphics, hero)
        state = EnemyState.IDLE
        graphics.color = Color.BLACK
    }
}