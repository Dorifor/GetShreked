import Renderer.WINDOW_HEIGHT
import Renderer.WINDOW_WIDTH
import java.awt.Color
import java.awt.Graphics2D

class Enemy(val posX: Int, val posY: Int, val size: Int, val color: Color) {
    fun draw(heroX: Int, heroY: Int, g: Graphics2D) {
        g.color = color
        g.fillOval(posX - heroX + WINDOW_WIDTH / 2 - size / 2, posY - heroY + WINDOW_HEIGHT / 2 - size / 2, size, size)
        g.color = Color.black
    }

}