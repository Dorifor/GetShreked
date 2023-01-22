import GUI.*
import GUI.Menu
import GameBoard.FRAMES_PER_SEC
import GameBoard.WINDOW_HEIGHT
import GameBoard.WINDOW_WIDTH
import GameBoard.currentMapLimit
import GameBoard.frame
import GameBoard.gamepad
import GameBoard.initMainMenu
import GameBoard.timer
import java.awt.*
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage
import java.io.File
import java.time.LocalDateTime
import javax.imageio.ImageIO
import javax.swing.JPanel
import kotlin.random.Random
import kotlin.system.exitProcess


enum class GameState {
    RUNNING, PAUSED, LEVEL_UP, FINISHED
}

class GameSession(val date: LocalDateTime, var map: Map) : JPanel() {
    var timeAlive: Int = 0
    var enemiesBeaten: Int = 0
    var bossesBeaten: Int = 0
    var state = GameState.RUNNING
    // private var currentMapLimit = 1012

    private val maxEnemies = 100

    var upPressed = false
    var downPressed = false
    var leftPressed = false
    var rightPressed = false

    var framesToNextEnemy = 0

    private val pickables = mutableListOf<Pickable>()
    private val enemies = mutableListOf<Enemy>()
    private val attacks = mutableListOf<Attack>()
    private val gui = mutableListOf<GUIElement>()
    private val pauseMenu = Menu()
    private val levelUpMenu = Menu()

    val hero = Hero(Vector(), 32)

    // RESSOURCES
    private val treeRessource: BufferedImage = ImageIO.read(File("src/main/resources/tree.png"))
    private val enemyResource: BufferedImage = ImageIO.read(File("src/main/resources/base_enemy.png"))
    private val donkeyEnemyResource: BufferedImage = ImageIO.read(File("src/main/resources/donkey_64.png"))
    private val donkeyEnemyHurtResource: BufferedImage = ImageIO.read(File("src/main/resources/donkey_hurt_64.png"))
    private val amogusEnemyResource: BufferedImage = ImageIO.read(File("src/main/resources/amogus_64.png"))
    private val amogusEnemyHurtResource: BufferedImage = ImageIO.read(File("src/main/resources/amogus_hurt_64.png"))
    private val zombieEnemyResource: BufferedImage = ImageIO.read(File("src/main/resources/zombie_64.png"))
    private val zombieEnemyHurtResource: BufferedImage = ImageIO.read(File("src/main/resources/zombie_hurt_64.png"))

    fun initGame() {
        revalidate()
        GameBoard.currentMapLimit = if (map.limit > 0) map.limit else currentMapLimit

        createAttacks()
        createPickables()
        initGUI()
        initPauseMenu()

        this.pickables.addAll(pickables)

        attacks.forEach { att ->
            hero.attacks.add(att)
        }

        attacks[0].initAttack(hero)

        // Set up key event handler
        frame.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                when (e.keyCode) {
                    KeyEvent.VK_UP -> upPressed = true
                    KeyEvent.VK_DOWN -> downPressed = true
                    KeyEvent.VK_LEFT -> leftPressed = true
                    KeyEvent.VK_RIGHT -> rightPressed = true
                    KeyEvent.VK_H -> isVisible = false
                    KeyEvent.VK_A -> hero.attacks.clear()
                    KeyEvent.VK_ESCAPE -> {
                        if (state == GameState.PAUSED) resumeGame()
                        else pauseGame()
                    }
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
        attacks.add(AreaOfEffectAttack("aoe", "an aoe attack that will draw an area around the player", 25, 1900))
        attacks.add(StaticAttack("static", "a static attack that will stay where it has been launched", 20, 10000))
        attacks.add(RangeAttack("bullets", "an aoe attack that will draw an area around the player", 15, 750, enemies))
    }

    private fun createPickables() {
        for (i in 1..30) { // pickables.add(
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
            ) // pickables.add(
            //     ExperiencePickable(
            //         Vector(
            //             Random.nextInt(-limit, limit).toDouble(),
            //             Random.nextInt(-limit, limit).toDouble()
            //         )
            //     )
            // )
        }
    }

    private fun spawnEnemy() {
        var randomX = hero.pos.x
        var randomY = hero.pos.y

        while (randomX in (hero.pos.x - WINDOW_WIDTH / 2)..(hero.pos.x + WINDOW_WIDTH / 2) || randomY in (hero.pos.y + WINDOW_HEIGHT / 2)..(hero.pos.y - WINDOW_HEIGHT / 2)) {
            randomX = Random.nextInt(-currentMapLimit, currentMapLimit).toDouble()
            randomY = Random.nextInt(-currentMapLimit, currentMapLimit).toDouble()
        }

        val enemyToAdd = if (hero.level <= 8) SpriteEnemy(
            Vector(randomX, randomY), 125, 3, 3.0, 35.0, enemyResource, 1.0, amogusEnemyResource, amogusEnemyHurtResource
        ) else if (hero.level <= 14) SpriteEnemy(
            Vector(randomX, randomY), 180, 5, 4.0, 35.0, enemyResource, 0.7, donkeyEnemyResource, donkeyEnemyHurtResource
        ) else SpriteEnemy(
            Vector(randomX, randomY), 230, 7, 4.5, 35.0, enemyResource, 0.7, zombieEnemyResource, zombieEnemyHurtResource
        )

        enemies.add(enemyToAdd)
    }

    private fun getGamepadInput() {
        if (gamepad?.controller == null) return
        gamepad?.update() // println(gamepad?.state?.leftStick)
        upPressed = gamepad?.state?.hat?.top == true || gamepad?.state?.leftStick?.y!! < 0.0
        downPressed = gamepad?.state?.hat?.bottom == true || gamepad?.state?.leftStick?.y!! > 0.0
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
            "map: ${map.name} ($currentMapLimit)"
        )


        graphics.color = Color.white
        debugs.forEachIndexed { i, s ->
            graphics.drawString(s, 15, 20 * (i + 1))
        }
        graphics.color = Color.black
    }

    private fun initGUI() {
        addMouseMotionListener(MouseMotionHandler())
        addMouseListener(MouseHandler())

        val healthBar = HealthBar(Point(WINDOW_WIDTH / 2 - 200 / 2, 20), 200, 26)
        healthBar.hero = hero
        val experienceBar =
            ExperienceBar(Point(WINDOW_WIDTH / 2 - 200 / 2, healthBar.pos.y + healthBar.height + 4), 200, 8, border = 1)
        experienceBar.hero = hero
        val levelText =
            LevelText(Point(WINDOW_WIDTH / 2 - 200 / 2, experienceBar.pos.y + experienceBar.height), "Level %s%")
        levelText.hero = hero
        val coinsText =
            CoinsText(Point(WINDOW_WIDTH / 2 + 200 / 2, experienceBar.pos.y + experienceBar.height), "%s% coins")
        coinsText.hero = hero

        gui.add(healthBar)
        gui.add(experienceBar)
        gui.add(levelText)
        gui.add(coinsText)
    }

    private fun updateGUI() {
        gui.forEach { el -> el.update() }
    }

    private fun initPauseMenu() {
        val btns = mapOf("Back" to { resumeGame() }, "Options" to { println("Options Menu") }, "Main Menu" to {
            timer.stop()
            initMainMenu()
        }, "Return to Desktop" to { exitProcess(0) })

        pauseMenu.generate(btns)
    }

    private fun initLevelUpMenu() {
        state = GameState.LEVEL_UP
        hero.attacks.forEach { it.state = AttackState.PAUSED }
        val buttons = mutableMapOf<String, () -> Unit>()
        attacks.forEach {
            if (it.level < 5) buttons["${it.name} -> ${it.level + 1}"] = {
                if (it.level == 0) it.initAttack(hero)
                else it.level++
                resumeGame()
            }
        }
        if (buttons.isEmpty()) {
            resumeGame()
            return
        }
        levelUpMenu.generate(buttons)
    }

    fun drawGUI(graphics: Graphics) {
        gui.forEach { el -> el.draw(graphics) }
    }

    fun pauseGame() {
        state = GameState.PAUSED
        hero.attacks.forEach { it.state = AttackState.PAUSED }
    }

    fun resumeGame() {
        state = GameState.RUNNING
        hero.attacks.forEach { it.state = it.previousState }
    }

    fun stepGame() {
        repaint()

        if (state == GameState.PAUSED) {
            pauseMenu.update()
            return
        }

        if (state == GameState.LEVEL_UP) {
            levelUpMenu.update()
            return
        }


        if (framesToNextEnemy >= FRAMES_PER_SEC / 1.5 && enemies.size <= maxEnemies) {
            for (i in 0..hero.level) {
                spawnEnemy()
                if (i < 5) spawnEnemy()
            }
            framesToNextEnemy = 0
        }

        // If enemies are to disappear this frame, drop experience and random chance of loot
        enemies.filter { it.toRemoveNextFrame }.forEach { e ->
            pickables.add(ExperiencePickable(e.pos, e.pos.y.toInt()))
            if (Random.nextInt(20) == 0) pickables.add(MoneyPickable(e.pos))
        } // Remove dead enemies
        enemies.removeAll { it.toRemoveNextFrame }
        if (enemies.size == 0) spawnEnemy()

        // Check if enemies are within attack range, hurt them if yes
        hero.attacks.filter { it.level != 0 }.forEach { att ->
            att.update(hero)
            enemies.forEach { en ->
                if (att.checkCollisions(en, hero)) {
                    en.hurt(att.getLevelDamage())
                    if (en.toRemoveNextFrame) enemiesBeaten++
                }
            }
        }

        // If hero have enough experience, level up & heal
        if (hero.experience >= hero.xpToNextLevel && hero.level < 20) {
            hero.levelUp()
            initLevelUpMenu()
        }

        enemies.forEach { enemy ->
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
        }

        // Clamp hero position to map limit
        hero.pos.x = hero.pos.x.coerceIn(-currentMapLimit.toDouble(), currentMapLimit.toDouble())
        hero.pos.y = hero.pos.y.coerceIn(-currentMapLimit.toDouble(), currentMapLimit.toDouble())


        pickables.forEach { it.goTowardHero(hero) }
        val picked = pickables.filter { hero.isColliding(it) }
        hero.coins += picked.filter { it is MoneyPickable }.size
        hero.health = (hero.health + picked.filter { it is FoodPickable }
            .fold(0) { sum, pickable -> sum + (pickable as FoodPickable).regen }).coerceIn(0..hero.maxHealth)
        hero.experience += picked.filter { it is ExperiencePickable }.size
        pickables.removeAll(picked)

        getGamepadInput()
        updateGUI()

        framesToNextEnemy++
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

        enemies.forEach { it.draw(g, hero) }

        drawGUI(g)
        drawDebug(g)

        if (state == GameState.LEVEL_UP) {
            levelUpMenu.draw(g)
        }

        if (state == GameState.PAUSED) {
            pauseMenu.draw(g)
        }
    }
}