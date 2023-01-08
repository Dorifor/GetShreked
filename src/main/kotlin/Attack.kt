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
    RUNNING, STOPPED
}

abstract class Attack(
    val name: String, val description: String, var level: Int, val damage: Int, val cooldownMillisec: Int = 1500
) {
    var frameCount = 0
    var pos = Vector(0.0, 0.0)
    var state: AttackState = AttackState.STOPPED

    fun initAttack(hero: Hero) {
        val stepTimer = Timer(cooldownMillisec) { launchAttack(hero) }
        stepTimer.start()
    }

    abstract fun checkCollisions(enemy: Enemy, hero: Hero): Boolean

    abstract fun launchAttack(hero: Hero)

    abstract fun draw(hero: Hero, graphics: Graphics)
}

class AreaOfEffectAttack(name: String, description: String, level: Int, damage: Int, cooldownMillisec: Int) :
    Attack(name, description, level, damage, cooldownMillisec) {
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

    override fun draw(hero: Hero, graphics: Graphics) {
        if (state == AttackState.STOPPED) return

        frameCount++ //        currentSize = (sin((0..frameLength).convert(frameCount, (0..Math.PI.toInt())).toDouble()) * 5).toInt().coerceIn(0..size)
        currentSize = (size * sin(((1.0 / 19) * frameCount))).toInt()
            .coerceIn(0..size) //        println("currentSize: $currentSize ")
        val greenAmount = (0..size).convert(currentSize, (0..200))
        graphics.color = Color(255 - greenAmount, 200, 255 - greenAmount, 128)
        graphics.color
        graphics.fillOval(
            WINDOW_WIDTH / 2 - currentSize / 2, WINDOW_HEIGHT / 2 - currentSize / 2, currentSize, currentSize
        )
        graphics.color = Color.BLACK

        if (currentSize <= 1 && frameCount > frameLength) {
            state = AttackState.STOPPED // println("stopped: $state")
        }
    }
}

class StaticAttack(name: String, description: String, level: Int, damage: Int, cooldownMillisec: Int) : Attack(
    name, description, level, damage, cooldownMillisec
) {
    val size: Int = 128;
    var currentSize = 0

    override fun checkCollisions(enemy: Enemy, hero: Hero): Boolean {
        return (frameCount % 30 == 0 && ceil(pos.distance(enemy.pos)) <= currentSize - currentSize / 4)
    }

    override fun launchAttack(hero: Hero) {
        frameCount = 0
        pos.x = hero.pos.x
        pos.y = hero.pos.y
    }

    override fun draw(hero: Hero, graphics: Graphics) {
        frameCount++
        currentSize = (frameCount * 10).coerceIn(0..size)
        val redAmount = (0..size).convert(currentSize, (0..200))
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
    name: String,
    description: String,
    level: Int,
    damage: Int,
    cooldownMillisec: Int,
    val enemies: MutableList<Enemy>
) : Attack(
    name, description, level, damage, cooldownMillisec
) {
    val size: Int = 16;
    var currentSize = 0
    var direction = Vector()
    val bullets = mutableListOf<RangeAttackBullet>()

    private fun getClosestEnemy(hero: Hero): Enemy? {
        return enemies.sortedWith( compareBy { it.pos.distance(hero.pos)})[0]
    }

    override fun checkCollisions(enemy: Enemy, hero: Hero): Boolean {
        bullets.forEach { bu ->
            if (ceil(bu.pos.distance(enemy.pos)) <= enemy.size) {
                // (enemy as BaseEnemy).color = Color.green
                bu.toDisappearNextFrame = true
                return true
            }
        }
        return false
    }

    override fun launchAttack(hero: Hero) {
        val target = getClosestEnemy(hero)
        if (target != null) {
            // (target as BaseEnemy).color = Color.magenta
            direction = Vector(target.pos)
            bullets.add(RangeAttackBullet(Vector(hero.pos), direction, 8, Color.RED))
        }
    }

    override fun draw(hero: Hero, graphics: Graphics) {
        bullets.removeAll { bu -> bu.toDisappearNextFrame }
        bullets.forEach { bu ->
            bu.update()
            bu.draw(hero, graphics)
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
            frame >= FRAMES_PER_SEC || pos.x <= -currentMapLimit || pos.x >= currentMapLimit || pos.y <= -currentMapLimit || pos.y >= currentMapLimit || pos.distance(direction) <= 8
    }

    fun draw(hero: Hero, graphics: Graphics) {
        graphics.color = color
        graphics.fillOval(
            pos.x.toInt() - hero.pos.x.toInt() + WINDOW_WIDTH / 2 - size / 2,
            pos.y.toInt() - hero.pos.y.toInt() + WINDOW_HEIGHT / 2 - size / 2,
            size,
            size
        )

        // graphics.drawLine(
        //     pos.x.toInt() - hero.pos.x.toInt() + WINDOW_WIDTH / 2 - size / 2,
        //     pos.y.toInt() - hero.pos.y.toInt() + WINDOW_HEIGHT / 2 - size / 2,
        //     direction.x.toInt() - hero.pos.x.toInt() + WINDOW_WIDTH / 2 - size / 2,
        //     direction.y.toInt() - hero.pos.y.toInt() + WINDOW_HEIGHT / 2 - size / 2,
        // )
    }
}

