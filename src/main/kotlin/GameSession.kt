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
    private val enemies = mutableListOf<Enemy>()
    private val attacks = mutableListOf<Attack>()
    private val hero = Hero(Vector(0, 0), 30)

    // RESSOURCES
    private val treeRessource: BufferedImage = ImageIO.read(File("src/main/resources/tree.png"))
    private val enemyRessource: BufferedImage = ImageIO.read(File("src/main/resources/base_enemy.png"))

    fun initGame(f: JFrame) {
        limit = if (map.limit > 0) map.limit else limit
        createPickables()
        createEnemies()
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


    private fun createPickables() {
        for (i in 1..30) {
            pickables.add(MoneyPickable(Random.nextInt(-limit, limit), Random.nextInt(-limit, limit)))
            pickables.add(FoodPickable(Random.nextInt(-limit, limit), Random.nextInt(-limit, limit)))
            pickables.add(ExperiencePickable(Random.nextInt(-limit, limit), Random.nextInt(-limit, limit)))
        }
    }

    private fun createEnemies() {
        for (i in 1..5) {
            enemies.add(BaseEnemy(Vector(Random.nextInt(100), Random.nextInt(100)), 100, 10, 10.0, 20, enemyRessource, 1.0, Color.RED))
        }
    }

    fun stepGame() {
        hero.attacks.forEach { att ->  }
        repaint()
    }

    fun drawDebug(g: Graphics) {
        val debugs = listOf(
            "hero: ${hero.pos.x}, ${hero.pos.y}",
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
            0 - hero.pos.x + GameBoard.WINDOW_WIDTH / 2 - map.image.width / 2,
            0 - hero.pos.y + GameBoard.WINDOW_HEIGHT / 2 - map.image.height / 2,
            null
        )

        g.drawImage(
            treeRessource,
            0 - hero.pos.x + GameBoard.WINDOW_WIDTH / 2 - treeRessource.width / 2,
            0 - hero.pos.y + GameBoard.WINDOW_HEIGHT / 2 - treeRessource.height / 2,
            null
        )

        // Draw the ennemies
        pickables.forEach { it.draw(hero.pos.x, hero.pos.y, g) }
        // Draw the hero
        hero.draw(g)

        enemies.forEach {enemy ->
            enemy.moveToHero(hero)
            enemy.draw(g, hero)
        }

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

        hero.pos.x = hero.pos.x.coerceIn(-limit, limit)
        hero.pos.y = hero.pos.y.coerceIn(-limit, limit)

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