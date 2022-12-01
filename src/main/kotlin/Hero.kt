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

class Hero(var posX: Int, var posY: Int, var size: Int) {
    val speed = 10 // Max distance per tick
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
        val distance = sqrt((posX - e.posX).toDouble().pow(2.0) + (posY - e.posY).toDouble().pow(2.0))
        // If the distance is less than the sum of the radius, the hero is colliding with the enemy
        return distance < (size / 2 + e.size / 2)
    }

    fun moveUp() {
        posY -= speed
    }
    fun moveDown() {
        posY += speed
    }
    fun moveLeft() {
        posX -= speed
    }
    fun moveRight() {
        posX += speed
    }

    fun moveUpLeft() {
        val speedX = speed * cos(Math.PI / 4)
        val speedY = speed * sin(Math.PI / 4)
        posX -= speedX.toInt()
        posY -= speedY.toInt()
    }
    fun moveUpRight() {
        val speedX = speed * cos(Math.PI / 4)
        val speedY = speed * sin(Math.PI / 4)
        posX += speedX.toInt()
        posY -= speedY.toInt()
    }
    fun moveDownLeft() {
        val speedX = speed * cos(Math.PI / 4)
        val speedY = speed * sin(Math.PI / 4)
        posX -= speedX.toInt()
        posY += speedY.toInt()
    }
    fun moveDownRight() {
        val speedX = speed * cos(Math.PI / 4)
        val speedY = speed * sin(Math.PI / 4)
        posX += speedX.toInt()
        posY += speedY.toInt()
    }
}