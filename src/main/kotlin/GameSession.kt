import GameBoard.WINDOW_HEIGHT
import GameBoard.WINDOW_WIDTH
import GameBoard.gamepad
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
    private var currentMapLimit = 1012

    private var xpToLevelUp = 30

    var upPressed = false
    var downPressed = false
    var leftPressed = false
    var rightPressed = false

    private val pickables = mutableListOf<Pickable>()
    private val enemies = mutableListOf<Enemy>()
    private val attacks = mutableListOf<Attack>()
    private val guiElements = mutableListOf<GUIElement>()
    private val hero = Hero(Vector(), 32)

    // RESSOURCES
    private val treeRessource: BufferedImage = ImageIO.read(File("src/main/resources/tree.png"))
    private val enemyRessource: BufferedImage = ImageIO.read(File("src/main/resources/base_enemy.png"))

    fun initGame(f: JFrame) {
        GameBoard.currentMapLimit = if (map.limit > 0) map.limit else currentMapLimit

        createEnemies()
        createAttacks()
        createPickables()

        initGUI()

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
                    KeyEvent.VK_A -> hero.attacks.clear()
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

    private fun initGUI() {
        val healthBar = HealthBar(Point(WINDOW_WIDTH / 2 - 200 / 2, 20), 200, 26)
        healthBar.hero = hero
        val experienceBar = ExperienceBar(Point(WINDOW_WIDTH / 2 - 200 / 2, 50), 200, 8, 0, xpToLevelUp, 1)
        experienceBar.hero = hero

        guiElements.add(healthBar)
        guiElements.add(experienceBar)
    }

    private fun updateGUI() {
        guiElements.forEach { el -> el.update() }
    }

    private fun createAttacks() {
        attacks.add(StaticAttack("static", "a static attack that will stay where it has been launched", 1, 30, 10000))
        attacks.add(AreaOfEffectAttack("aoe", "an aoe attack that will draw an area around the player", 1, 25, 1900))
        attacks.add(RangeAttack("aoe", "an aoe attack that will draw an area around the player", 1, 20, 750, enemies))
    }


    private fun createPickables() {
        for (i in 1..30) {
            // pickables.add(
            //     MoneyPickable(
            //         Vector(
            //             Random.nextInt(-limit, limit).toDouble(),
            //             Random.nextInt(-limit, limit).toDouble()
            //         )
            //     )
            // )
            pickables.add(
                FoodPickable(
                    Vector(
                        Random.nextInt(-currentMapLimit, currentMapLimit).toDouble(),
                        Random.nextInt(-currentMapLimit, currentMapLimit).toDouble()
                    )
                )
            )
            // pickables.add(
            //     ExperiencePickable(
            //         Vector(
            //             Random.nextInt(-limit, limit).toDouble(),
            //             Random.nextInt(-limit, limit).toDouble()
            //         )
            //     )
            // )
        }
    }

    private fun createEnemies() {
        for (i in 1..30) {
            enemies.add(
                BaseEnemy(
                    Vector(
                        Random.nextDouble(-currentMapLimit.toDouble(), currentMapLimit.toDouble()),
                        Random.nextDouble(-currentMapLimit.toDouble(), currentMapLimit.toDouble())
                    ),
                    150,
                    3,
                    3.5,
                    35.0,
                    enemyRessource,
                    1.0,
                    Color.RED
                )
            )
        }
    }

    fun stepGame() {
        // If enemies are to disappear this frame, drop experience and random chance of loot
        enemies.filter { it.toRemoveNextFrame }.forEach { e ->
            pickables.add(ExperiencePickable(e.pos, e.pos.y.toInt()))
            if (Random.nextInt(20) == 0) pickables.add(MoneyPickable(e.pos))
        }
        // Remove dead enemies
        enemies.removeAll { it.toRemoveNextFrame }
        if (enemies.size == 0) createEnemies()

        // Check if enemies are within attack range, hurt them if yes
        hero.attacks.forEach { att ->
            enemies.forEach { en ->
                if (att.checkCollisions(en, hero)) {
                    en.hurt(att.damage)
                }
            }
        }

        // If hero have enough experience, level up & heal
        if (hero.experience >= xpToLevelUp) {
            val extra = hero.experience - xpToLevelUp
            hero.experience = extra
            hero.level++
            hero.health = hero.maxHealth
        }

        getGamepadInput()
        updateGUI()
        repaint()
    }

    private fun getGamepadInput() {
        gamepad?.update()
        // println(gamepad?.state?.leftStick)
        upPressed = gamepad?.state?.hat?.top == true || gamepad?.state?.leftStick?.y!! < 0.0
        downPressed = gamepad?.state?.hat?.bottom == true || gamepad?.state?.leftStick?.y!! >  0.0
        leftPressed = gamepad?.state?.hat?.left == true || gamepad?.state?.leftStick?.x!! < 0.0
        rightPressed = gamepad?.state?.hat?.right == true || gamepad?.state?.leftStick?.x!! > 0.0
    }

    private fun drawDebug(graphics: Graphics) {
        val debugs = listOf(
            "hero: ${hero.pos.x.toInt()}, ${hero.pos.y.toInt()}",
            "level: ${hero.level}",
            "coins: ${hero.coins}",
            "health: ${hero.health}",
            "experience: ${hero.experience}",
            "map: ${map.name} ($currentMapLimit)",
            "enemy distance: ${hero.pos.distance(enemies[0].pos)}"
        )


        graphics.color = Color.white
        debugs.forEachIndexed { i, s ->
            graphics.drawString(s, 15, 20 * (i + 1))
        }
        graphics.color = Color.black
    }

    private fun drawGUI(graphics: Graphics) {
        guiElements.forEach { el -> el.draw(graphics) }
    }


    override fun paint(gg: Graphics) {
        super.paintComponent(gg)
        val g = gg as Graphics2D
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT)

        g.drawImage(
            map.image,
            0 - hero.pos.x.toInt() + WINDOW_WIDTH / 2 - map.image.width / 2,
            0 - hero.pos.y.toInt() + WINDOW_HEIGHT / 2 - map.image.height / 2,
            null
        )

        g.drawImage(
            treeRessource,
            0 - hero.pos.x.toInt() + WINDOW_WIDTH / 2 - treeRessource.width / 2,
            0 - hero.pos.y.toInt() + WINDOW_HEIGHT / 2 - treeRessource.height / 2,
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

        hero.pos.x = hero.pos.x.coerceIn(-currentMapLimit.toDouble(), currentMapLimit.toDouble())
        hero.pos.y = hero.pos.y.coerceIn(-currentMapLimit.toDouble(), currentMapLimit.toDouble())

        // Check if the hero is in collision with an enemy
        pickables.forEach { it.goTowardHero(hero) }
        val picked = pickables.filter { hero.isColliding(it) }
        hero.coins += picked.filter { it is MoneyPickable }.size
        hero.health = (hero.health + picked.filter { it is FoodPickable }
            .fold(0) { sum, pickable -> sum + (pickable as FoodPickable).regen }).coerceIn(0..hero.maxHealth)
        hero.experience += picked.filter { it is ExperiencePickable }.size
        pickables.removeAll(picked)

        drawGUI(g)
        drawDebug(g)
    }
}