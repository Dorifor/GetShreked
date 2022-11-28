import Renderer.WINDOW_HEIGHT
import Renderer.WINDOW_WIDTH
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class Hero(var posX: Int, var posY: Int, var size: Int) {
    val speed = 10 // Max distance per tick
    var coins = 0

    val sprite: BufferedImage = ImageIO.read(File("src/main/resources/shrok.png"))

    fun draw(g: Graphics2D) {
        val centerX = WINDOW_WIDTH / 2
        val centerY = WINDOW_HEIGHT / 2
        // Draw the hero always in the center of the screen
//        g.color = Color.blue
//        g.fillOval( centerX - 15, centerY - 15, 30, 30)
//        g.color = Color.white
//        g.fillOval( centerX - 10, centerY - 10, 20, 20)
//        g.color = Color.red
//        g.fillOval( centerX - 5, centerY - 5, 10, 10)

        g.drawImage(sprite, centerX - 16, centerY - 16, null)
    }

    fun isColliding(e: Enemy): Boolean {
        // Computes the distance between the hero and the enemy
        val distance = Math.sqrt(Math.pow((posX - e.posX).toDouble(), 2.0) + Math.pow((posY - e.posY).toDouble(), 2.0))
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
        val speedX = speed * Math.cos(Math.PI / 4)
        val speedY = speed * Math.sin(Math.PI / 4)
        posX -= speedX.toInt()
        posY -= speedY.toInt()
    }
    fun moveUpRight() {
        val speedX = speed * Math.cos(Math.PI / 4)
        val speedY = speed * Math.sin(Math.PI / 4)
        posX += speedX.toInt()
        posY -= speedY.toInt()
    }
    fun moveDownLeft() {
        val speedX = speed * Math.cos(Math.PI / 4)
        val speedY = speed * Math.sin(Math.PI / 4)
        posX -= speedX.toInt()
        posY += speedY.toInt()
    }
    fun moveDownRight() {
        val speedX = speed * Math.cos(Math.PI / 4)
        val speedY = speed * Math.sin(Math.PI / 4)
        posX += speedX.toInt()
        posY += speedY.toInt()
    }
}