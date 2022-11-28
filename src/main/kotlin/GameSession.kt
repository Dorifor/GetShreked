import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage
import java.io.File
import java.time.LocalDateTime
import java.util.*
import javax.imageio.ImageIO
import javax.swing.JFrame
import javax.swing.JPanel
import kotlin.random.Random

class GameSession(val date: LocalDateTime, val map: Map) : JPanel() {
    var timeAlive: Int = 0
    var experience: Int = 0
    var level: Int = 0
    var money: Int = 0
    var enemiesBeaten: Int = 0
    var bossesBeaten: Int = 0
    var posX: Int = 0
    var posY: Int = 0
    var speedFactor: Double = 1.0
    val gameSessions = mutableListOf<GameSession>()
    val LIMIT = 1012


    var upPressed = false
    var downPressed = false
    var leftPressed = false
    var rightPressed = false
    val pickables = mutableListOf<Pickable>()
    val hero = Hero(0, 0, 30)

    val ground: BufferedImage = ImageIO.read(File("src/main/resources/mapx2v2.png"))
    val tree: BufferedImage = ImageIO.read(File("src/main/resources/tree.png"))

    fun initGame(f: JFrame) {
        createEnnemies()
        this.pickables.addAll(pickables)

        // Set up key event handler
        f.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                when (e.keyCode) {
                    KeyEvent.VK_UP -> upPressed = true
                    KeyEvent.VK_DOWN -> downPressed = true
                    KeyEvent.VK_LEFT -> leftPressed = true
                    KeyEvent.VK_RIGHT -> rightPressed = true
                    KeyEvent.VK_H -> isVisible = false
                }
            }

            override fun keyReleased(e: KeyEvent) {
                when (e.keyCode) {
                    KeyEvent.VK_UP -> upPressed = false
                    KeyEvent.VK_DOWN -> downPressed = false
                    KeyEvent.VK_LEFT -> leftPressed = false
                    KeyEvent.VK_RIGHT -> rightPressed = false
                }
            }
        })
    }


    private fun createEnnemies() {
        val entities = mutableListOf<Pickable>()
        for (i in 1..30) {
            entities.add(MoneyPickable(Random.nextInt(-LIMIT, LIMIT), Random.nextInt(-LIMIT, LIMIT)))
            entities.add(FoodPickable(Random.nextInt(-LIMIT, LIMIT), Random.nextInt(-LIMIT, LIMIT)))
            entities.add(ExperiencePickable(Random.nextInt(-LIMIT, LIMIT), Random.nextInt(-LIMIT, LIMIT)))
        }
        this.pickables.addAll(entities)
    }

    fun stepGame() {
        repaint()
    }

    fun drawDebug(g: Graphics) {
        g.color = Color.white
        g.drawString("hero: ${hero.posX}, ${hero.posY}", 10, 10)
        g.drawString("coins: ${hero.coins}", 10, 20)
        g.drawString("health: ${hero.health}", 10, 30)
        g.drawString("experience: ${hero.experience}", 10, 40)
        g.drawString("LIMIT: ${LIMIT}", 10, 50)
        g.color = Color.black
    }


    override fun paint(gg: Graphics) {
        super.paintComponent(gg)
        val g = gg as Graphics2D
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g.drawImage(
            ground,
            0 - hero.posX + GameBoard.WINDOW_WIDTH / 2 - ground.width / 2,
            0 - hero.posY + GameBoard.WINDOW_HEIGHT / 2 - ground.height / 2,
            null
        )

        g.drawImage(
            tree,
            0 - hero.posX + GameBoard.WINDOW_WIDTH / 2 - tree.width / 2,
            0 - hero.posY + GameBoard.WINDOW_HEIGHT / 2 - tree.height / 2,
            null
        )

        // Draw the ennemies
        pickables.forEach { it.draw(hero.posX, hero.posY, g) }
        // Draw the hero
        hero.draw(g)

        // Move the Hero
        if (upPressed && leftPressed) {
            hero.moveUpLeft()
        } else if (upPressed && rightPressed) {
            hero.moveUpRight()
        } else if (downPressed && leftPressed) {
            hero.moveDownLeft()
        } else if (downPressed && rightPressed) {
            hero.moveDownRight()
        } else if (upPressed) {
            hero.moveUp()
        } else if (downPressed) {
            hero.moveDown()
        } else if (leftPressed) {
            hero.moveLeft()
        } else if (rightPressed) {
            hero.moveRight()
        } else {
            // Invalid moves
        }

        hero.posX = hero.posX.coerceIn(-LIMIT, LIMIT)
        hero.posY = hero.posY.coerceIn(-LIMIT, LIMIT)

        // Check if the hero is in collision with an enemy
        val picked = pickables.filter { hero.isColliding(it) }
        hero.coins += picked.filter { it is MoneyPickable }.size
        hero.health = (hero.health + picked.filter { it is FoodPickable }
            .fold(0) { sum, pickable -> sum + (pickable as FoodPickable).regen }).coerceIn(0..100)
        hero.experience += picked.filter { it is ExperiencePickable }.size
        pickables.removeAll(picked)

        drawDebug(g)
    }
}