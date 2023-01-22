import GameBoard.FRAMES_PER_SEC
import GameBoard.WINDOW_HEIGHT
import GameBoard.WINDOW_WIDTH
import GameBoard.convert
import GameBoard.currentMapLimit
import java.awt.Color
import java.awt.Graphics
import javax.swing.Timer
import kotlin.math.ceil
import kotlin.math.sin

enum class AttackState {
    RUNNING, STOPPED, PAUSED
}

abstract class Attack(
    val name: String, val description: String, val damage: Int, val cooldownMillisec: Int = 1500
) {
    var level: Int = 0
    var frameCount = 0
    var pos = Vector(0.0, 0.0)
    var state: AttackState = AttackState.RUNNING
    var previousState: AttackState = state

    fun initAttack(hero: Hero) {
        level++
        launchAttack(hero)
        val stepTimer = Timer(cooldownMillisec) {
            if (state != AttackState.PAUSED) launchAttack(hero)
        }
        stepTimer.start()
    }

    abstract fun checkCollisions(enemy: Enemy, hero: Hero): Boolean

    abstract fun launchAttack(hero: Hero)

    abstract fun update(hero: Hero)

    abstract fun draw(hero: Hero, graphics: Graphics)

    abstract fun getLevelDamage() : Int
}

class AreaOfEffectAttack(name: String, description: String, damage: Int, cooldownMillisec: Int) :
    Attack(name, description, damage, cooldownMillisec) {
    val size: Int = 96;
    var currentSize = 0
    var frameLength = 120

    override fun checkCollisions(enemy: Enemy, hero: Hero): Boolean {
        return (frameCount % 5 == 0 && ceil(hero.pos.distance(enemy.pos)) <= currentSize - 10 && state == AttackState.RUNNING)
    }

    override fun launchAttack(hero: Hero) {
        frameCount = 0
        state = AttackState.RUNNING // println("started again: $cooldownMillisec")
    }

    override fun update(hero: Hero) {
        val levelChange = 16 * (level - 1)
        if (state != AttackState.RUNNING) return
        // currentSize = (sin((0..frameLength).convert(frameCount, (0..Math.PI.toInt())).toDouble()) * 5).toInt().coerceIn(0..size)
        currentSize = ((size + levelChange) * sin(((1.0 / 19) * frameCount))).toInt().coerceIn(0..size + levelChange)


        if (currentSize <= 1 && frameCount > frameLength) {
            state = AttackState.STOPPED
        }
        previousState = state

        frameCount++
    }

    override fun getLevelDamage() : Int {
        val levelChange = 12 * (level - 1)
        return damage + levelChange
    }

    override fun draw(hero: Hero, graphics: Graphics) {
        val levelChange = 16 * (level - 1)
        val greenAmount = (0..size + levelChange).convert(currentSize, (0..200))
        graphics.color = Color(255 - greenAmount, 200, 255 - greenAmount, 128)
        // graphics.color
        graphics.fillOval(
            WINDOW_WIDTH / 2 - currentSize / 2, WINDOW_HEIGHT / 2 - currentSize / 2, currentSize, currentSize
        )
        graphics.color = Color.BLACK
    }
}

class StaticAttack(name: String, description: String, damage: Int, cooldownMillisec: Int) : Attack(
    name, description, damage, cooldownMillisec
) {
    val size: Int = 128;
    var currentSize = 0

    override fun checkCollisions(enemy: Enemy, hero: Hero): Boolean {
        return (frameCount % 30 == 0 && ceil(pos.distance(enemy.pos)) <= currentSize - currentSize / 4)
    }

    override fun launchAttack(hero: Hero) {
        println("SHEEEESH LAUNCH ATTACK STATIC")
        frameCount = 0
        pos.x = hero.pos.x
        pos.y = hero.pos.y
    }

    override fun update(hero: Hero) {
        val levelChange = 24 * (level - 1)
        if (state != AttackState.RUNNING) return
        currentSize = (frameCount * 10).coerceIn(0..size + levelChange)
        previousState = state
        frameCount++
    }

    override fun getLevelDamage() : Int {
        val levelChange = 8 * (level - 1)
        return damage + levelChange
    }

    override fun draw(hero: Hero, graphics: Graphics) {
        val levelChange = 24 * (level - 1)
        val redAmount = (0..size + levelChange).convert(currentSize, (0..200))
        graphics.color = Color(200, 255 - redAmount, 255 - redAmount, 128)
        graphics.fillOval(
            pos.x.toInt() - hero.pos.x.toInt() + WINDOW_WIDTH / 2 - currentSize / 2,
            pos.y.toInt() - hero.pos.y.toInt() + WINDOW_HEIGHT / 2 - currentSize / 2,
            currentSize,
            currentSize
        )
        graphics.color = Color.BLACK
    }
}


class RangeAttack(
    name: String, description: String, damage: Int, cooldownMillisec: Int, val enemies: MutableList<Enemy>
) : Attack(
    name, description, damage, cooldownMillisec
) {
    val size: Int = 16;
    var currentSize = 0
    var direction = Vector()
    val bullets = mutableListOf<RangeAttackBullet>()

    private fun getClosestEnemy(hero: Hero, offset: Int = 0): Enemy? {
        return enemies.sortedWith(compareBy { it.pos.distance(hero.pos) })[offset]
    }

    override fun checkCollisions(enemy: Enemy, hero: Hero): Boolean {
        bullets.forEach { bu ->
            if (ceil(bu.pos.distance(enemy.pos)) <= enemy.size) { // (enemy as BaseEnemy).color = Color.green
                bu.toDisappearNextFrame = true
                return true
            }
        }
        return false
    }

    override fun launchAttack(hero: Hero) {
        val isAnyEnemyOnMap = getClosestEnemy(hero) != null
        if (isAnyEnemyOnMap) { // (target as BaseEnemy).color = Color.magenta
            for (i in 0 until level) {
                val target = getClosestEnemy(hero, i)
                if (target != null) {
                    direction = Vector(target.pos)
                    bullets.add(RangeAttackBullet(Vector(hero.pos), direction, 8, Color.RED))
                }
            }
        }
    }

    override fun update(hero: Hero) {
        if (state != AttackState.RUNNING) return
        bullets.removeAll { bu -> bu.toDisappearNextFrame }
        bullets.forEach { bu -> bu.update() }
        previousState = state
    }

    override fun getLevelDamage() : Int {
        val levelChange = 5 * (level - 1)
        return damage + levelChange
    }

    override fun draw(hero: Hero, graphics: Graphics) {
        bullets.forEach { bu ->
            bu.draw(hero, graphics, level)
        }

        graphics.color = Color.BLACK
    }
}

class RangeAttackBullet(var pos: Vector, var direction: Vector, val size: Int, val color: Color) {
    var toDisappearNextFrame: Boolean = false
    var frame = 0
    fun update() {
        frame++
        var force = Vector(direction)
        force -= pos
        force.normalize()
        force *= 15.0

        pos += force
        toDisappearNextFrame =
            frame >= FRAMES_PER_SEC || pos.x <= -currentMapLimit || pos.x >= currentMapLimit || pos.y <= -currentMapLimit || pos.y >= currentMapLimit || pos.distance(
                direction
            ) <= 8
    }

    fun draw(hero: Hero, graphics: Graphics, level: Int) {
        val levelChange = 1 * (level - 1)
        graphics.color = color
        graphics.fillOval(
            pos.x.toInt() - hero.pos.x.toInt() + WINDOW_WIDTH / 2 - size / 2,
            pos.y.toInt() - hero.pos.y.toInt() + WINDOW_HEIGHT / 2 - size / 2,
            size + levelChange,
            size + levelChange
        )

        // graphics.drawLine(
        //     pos.x.toInt() - hero.pos.x.toInt() + WINDOW_WIDTH / 2 - size / 2,
        //     pos.y.toInt() - hero.pos.y.toInt() + WINDOW_HEIGHT / 2 - size / 2,
        //     direction.x.toInt() - hero.pos.x.toInt() + WINDOW_WIDTH / 2 - size / 2,
        //     direction.y.toInt() - hero.pos.y.toInt() + WINDOW_HEIGHT / 2 - size / 2,
        // )
    }
}

