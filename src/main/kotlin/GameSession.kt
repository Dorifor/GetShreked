import java.awt.*
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage
import java.io.File
import java.time.LocalDateTime
import javax.imageio.ImageIO
import javax.swing.JFrame
import javax.swing.JPanel
import kotlin.random.Random

class GameSession(val date: LocalDateTime, var map: Map) : JPanel() {
    var timeAlive: Int = 0
    var enemiesBeaten: Int = 0
    var bossesBeaten: Int = 0
    var speedFactor: Double = 1.0
    private var limit = 1012

    var upPressed = false
    var downPressed = false
    var leftPressed = false
    var rightPressed = false

    private val pickables = mutableListOf<Pickable>()
    private val attacks = mutableListOf<Attack>()
    private val hero = Hero(0, 0, 30)

    private val tree: BufferedImage = ImageIO.read(File("src/main/resources/tree.png"))

    fun initGame(f: JFrame) {
        limit = if (map.limit > 0) map.limit else limit
        createEnnemies()
        createAttacks()

        this.pickables.addAll(pickables)

        hero.attacks.add(attacks[0])
        hero.attacks.add(attacks[1])
        hero.attacks[0].initAttack(hero)
        hero.attacks[1].initAttack(hero)

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

    private fun createAttacks() {
        attacks.add(StaticAttack("static", "a static attack that will stay where it has been launched", 1, 10, 6000))
        attacks.add(AreaOfEffectAttack("aoe", "an aoe attack that will draw an area around the player", 1, 10, 3500))
    }


    private fun createEnnemies() {
        val entities = mutableListOf<Pickable>()
        for (i in 1..30) {
            entities.add(MoneyPickable(Random.nextInt(-limit, limit), Random.nextInt(-limit, limit)))
            entities.add(FoodPickable(Random.nextInt(-limit, limit), Random.nextInt(-limit, limit)))
            entities.add(ExperiencePickable(Random.nextInt(-limit, limit), Random.nextInt(-limit, limit)))
        }
        this.pickables.addAll(entities)
    }

    fun stepGame() {
        hero.attacks.forEach { att ->  }
        repaint()
    }

    fun drawDebug(g: Graphics) {
        val debugs = listOf(
            "hero: ${hero.posX}, ${hero.posY}",
            "coins: ${hero.coins}",
            "health: ${hero.health}",
            "experience: ${hero.experience}",
            "map: ${map.name} (${limit})"
        )


        g.color = Color.white
        debugs.forEachIndexed { i, s ->
            g.drawString(s, 15, 20 * (i + 1))
        }
        g.color = Color.black
    }


    override fun paint(gg: Graphics) {
        super.paintComponent(gg)
        val g = gg as Graphics2D
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g.drawImage(
            map.image,
            0 - hero.posX + GameBoard.WINDOW_WIDTH / 2 - map.image.width / 2,
            0 - hero.posY + GameBoard.WINDOW_HEIGHT / 2 - map.image.height / 2,
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

        hero.posX = hero.posX.coerceIn(-limit, limit)
        hero.posY = hero.posY.coerceIn(-limit, limit)

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