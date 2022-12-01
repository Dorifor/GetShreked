import java.awt.Color
import java.awt.image.BufferedImage

abstract class Enemy(
    val posX: Int,
    val posY: Int,
    var health: Int,
    var damage: Int,
    var speed: Double,
    var size: Double,
    val sprite: BufferedImage,
    val attackInterval: Double
) {
    fun calculateTrajectory() {

    }

    fun attack() {

    }

    open fun hurt(damage: Int) {

    }
}

class BossEnemy(
    posX: Int,
    posY: Int,
    health: Int,
    damage: Int,
    speed: Double,
    size: Double,
    sprite: BufferedImage,
    attackInterval: Double
) : Enemy(posX, posY, damage, health, speed, size, sprite, attackInterval)


class BaseEnemy(
    posX: Int,
    posY: Int,
    health: Int,
    damage: Int,
    speed: Double,
    size: Double,
    sprite: BufferedImage,
    attackInterval: Double,
    color: Color
) : Enemy(posX, posY, damage, health, speed, size, sprite, attackInterval)