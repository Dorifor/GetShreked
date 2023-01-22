import GameBoard.WINDOW_HEIGHT
import GameBoard.WINDOW_WIDTH
import GameBoard.initDeathMenu
import GameBoard.timer
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sqrt

enum class HeroState {
    IDLE,
    HURT
}

class Hero(var pos: Vector, var size: Int) {
    val speed: Double = 10.0 // Max distance per tick
    var coins = 0
    var experience = 0
    var level = 1
    var speedFactor = 1.0
    var attractDistance = 120
    var maxHealth = 100
    // var health = maxHealth
    var health = 0
    val attacks = mutableListOf<Attack>()
    var state = HeroState.IDLE
    // var xpToNextLevel = 30
    var xpToNextLevel = 5

    private val spriteIdle: BufferedImage = ImageIO.read(File("src/main/resources/shrok.png"))
    private val spriteHurt: BufferedImage = ImageIO.read(File("src/main/resources/shrok_hurt.png"))

    private var sprite = spriteIdle

    fun draw(g: Graphics2D) {
        val centerX = WINDOW_WIDTH / 2
        val centerY = WINDOW_HEIGHT / 2
        attacks.filter { it.level != 0 }.forEach { it.draw(this, g) }
        sprite = if (state == HeroState.HURT) spriteHurt else spriteIdle
        g.drawImage(sprite, centerX - 16, centerY - 16, null)
        state = HeroState.IDLE
    }

    fun isColliding(e: Pickable): Boolean {
        // Computes the distance between the hero and the enemy
        val distance = sqrt((pos.x - e.pos.x).pow(2.0) + (pos.y - e.pos.y).pow(2.0))
        // If the distance is less than the sum of the radius, the hero is colliding with the enemy
        return distance < (size / 2 + e.size / 2)
    }

    fun hurt(damage: Int) {
        state = HeroState.HURT
        health = (health - damage).coerceIn(0..maxHealth)
        if (health <= 0) {
            timer.stop()
            initDeathMenu()
        }
    }

    fun levelUp() {
        val extra = experience - xpToNextLevel
        experience = extra
        maxHealth += 15
        health = maxHealth
        level++
        xpToNextLevel = 15 * level +  30
    }

    fun moveUp() {
        pos.y -= speed
    }

    fun moveDown() {
        pos.y += speed
    }

    fun moveLeft() {
        pos.x -= speed
    }

    fun moveRight() {
        pos.x += speed
    }

    fun moveUpLeft() {
        val diagSpeed = (speed * cos(Math.PI / 4))
        pos += Vector(-diagSpeed, -diagSpeed)
    }

    fun moveUpRight() {
        val diagSpeed = (speed * cos(Math.PI / 4))
        pos += (Vector(diagSpeed, -diagSpeed))
    }

    fun moveDownLeft() {
        val diagSpeed = (speed * cos(Math.PI / 4))
        pos += (Vector(-diagSpeed, diagSpeed))
    }

    fun moveDownRight() {
        val diagSpeed = (speed * cos(Math.PI / 4))
        pos += (Vector(diagSpeed, diagSpeed))
    }
}