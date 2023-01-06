import GameBoard.WINDOW_HEIGHT
import GameBoard.WINDOW_WIDTH
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class Hero(val pos: Vector, var size: Int) {
    val speed: Double = 10.0 // Max distance per tick
    var coins = 0
    var health = 50
    var experience = 0
    var level = 1
    var speedFactor = 1.0
    val attacks = mutableListOf<Attack>()

    private val sprite: BufferedImage = ImageIO.read(File("src/main/resources/shrok.png"))

    fun draw(g: Graphics2D) {
        val centerX = WINDOW_WIDTH / 2
        val centerY = WINDOW_HEIGHT / 2
        attacks.forEach { it.draw(this, g) }
        g.drawImage(sprite, centerX - 16, centerY - 16, null)
    }

    fun isColliding(e: Pickable): Boolean {
        // Computes the distance between the hero and the enemy
        val distance = sqrt((pos.x - e.pos.x).pow(2.0) + (pos.y - e.pos.y).pow(2.0))
        // If the distance is less than the sum of the radius, the hero is colliding with the enemy
        return distance < (size / 2 + e.size / 2)
    }

    fun moveUp() {
        pos.sub(Vector(0.0, speed))
    }

    fun moveDown() {
        pos.add(Vector(0.0, speed))
    }

    fun moveLeft() {
        pos.sub(Vector(speed, 0.0))
    }

    fun moveRight() {
        pos.add(Vector(speed, 0.0))
    }

    fun moveUpLeft() {
        val diagSpeed = (speed * cos(Math.PI / 4))
        pos.add(Vector(-diagSpeed, -diagSpeed))
    }

    fun moveUpRight() {
        val diagSpeed = (speed * cos(Math.PI / 4))
        pos.add(Vector(diagSpeed, -diagSpeed))
    }

    fun moveDownLeft() {
        val diagSpeed = (speed * cos(Math.PI / 4))
        pos.add(Vector(-diagSpeed, diagSpeed))
    }

    fun moveDownRight() {
        val diagSpeed = (speed * cos(Math.PI / 4))
        pos.add(Vector(diagSpeed, diagSpeed))
    }
}