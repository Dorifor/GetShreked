import java.awt.*
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage
import java.io.File
import java.time.LocalDateTime
import javax.imageio.ImageIO
import javax.swing.JFrame
import javax.swing.JPanel
import kotlin.math.ceil
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
    private val hero = Hero(Vector(0.0, 0.0), 32)

    // RESSOURCES
    private val treeRessource: BufferedImage = ImageIO.read(File("src/main/resources/tree.png"))
    private val enemyRessource: BufferedImage = ImageIO.read(File("src/main/resources/base_enemy.png"))

    fun initGame(f: JFrame) {
        limit = if (map.limit > 0) map.limit else limit
//        createPickables()
        createEnemies()
        createAttacks()

        this.pickables.addAll(pickables)

        attacks.forEach { att ->
            hero.attacks.add(att)
            att.initAttack(hero)
        }

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
//        attacks.add(StaticAttack("static", "a static attack that will stay where it has been launched", 1, 30, 10000))
        attacks.add(AreaOfEffectAttack("aoe", "an aoe attack that will draw an area around the player", 1, 25, 1900))
    }


    private fun createPickables() {
        for (i in 1..30) {
            pickables.add(MoneyPickable(Vector(Random.nextInt(-limit, limit).toDouble(), Random.nextInt(-limit, limit).toDouble())))
            pickables.add(FoodPickable(Vector(Random.nextInt(-limit, limit).toDouble(), Random.nextInt(-limit, limit).toDouble())))
            pickables.add(ExperiencePickable(Vector(Random.nextInt(-limit, limit).toDouble(), Random.nextInt(-limit, limit).toDouble())))
        }
    }

    private fun createEnemies() {
        for (i in 1..1) {
            enemies.add(
                BaseEnemy(
                    Vector(
                        Random.nextDouble(-limit.toDouble(), limit.toDouble()),
                        Random.nextDouble(-limit.toDouble(), limit.toDouble())
                    ),
                    150,
                    10,
                    3.5,
                    35,
                    enemyRessource,
                    1.0,
                    Color.RED
                )
            )
        }
    }

    fun stepGame() {
        enemies.filter { it.toRemoveNextFrame }.forEach { e ->
            pickables.add(ExperiencePickable(e.pos, e.pos.y.toInt()))
            if (Random.nextInt(10) == 10) pickables.add(MoneyPickable(e.pos))
        }
        enemies.removeAll { it.toRemoveNextFrame }
        if (enemies.size == 0) createEnemies()

        hero.attacks.forEach { att ->
            enemies.forEach { en ->
                if (att.checkCollisions(en, hero)) {
//                    println("en: ${en.pos} - ${en.health}")
//                    println("pl: ${hero.pos}")
                    en.hurt(att.damage)
                }
            }
        }
        repaint()
    }

    fun drawDebug(g: Graphics) {
        val debugs = listOf(
            "hero: ${hero.pos.x.toInt()}, ${hero.pos.y.toInt()}",
            "coins: ${hero.coins}",
            "health: ${hero.health}",
            "experience: ${hero.experience}",
            "map: ${map.name} (${limit})",
            "enemy distance: ${hero.pos.dist(enemies[0].pos)}"
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
            0 - hero.pos.x.toInt() + GameBoard.WINDOW_WIDTH / 2 - map.image.width / 2,
            0 - hero.pos.y.toInt() + GameBoard.WINDOW_HEIGHT / 2 - map.image.height / 2,
            null
        )

        g.drawImage(
            treeRessource,
            0 - hero.pos.x.toInt() + GameBoard.WINDOW_WIDTH / 2 - treeRessource.width / 2,
            0 - hero.pos.y.toInt() + GameBoard.WINDOW_HEIGHT / 2 - treeRessource.height / 2,
            null
        )

        pickables.forEach { it.draw(hero.pos.x.toInt(), hero.pos.y.toInt(), g) }
        hero.draw(g)

        enemies.forEach { enemy ->
            enemy.draw(g, hero)
            enemy.manageCollisions(enemies.filter { it != enemy })
            enemy.moveToHero(hero)
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

        hero.pos.x = hero.pos.x.coerceIn(-limit.toDouble(), limit.toDouble())
        hero.pos.y = hero.pos.y.coerceIn(-limit.toDouble(), limit.toDouble())

        // Check if the hero is in collision with an enemy
        val picked = pickables.filter { hero.isColliding(it) }
        hero.coins += picked.filter { it is MoneyPickable }.size
        hero.health = (hero.health + picked.filter { it is FoodPickable }
            .fold(0) { sum, pickable -> sum + (pickable as FoodPickable).regen }).coerceIn(0..100)
        hero.experience += picked.filter { it is ExperiencePickable }.size
        pickables.removeAll(picked)

        drawDebug(g)
//        gg.drawLine(ceil(hero.pos.x).toInt(), ceil(hero.pos.y).toInt(), ceil(enemies[0].pos.x).toInt(), ceil(enemies[0].pos.y).toInt())
    }
}