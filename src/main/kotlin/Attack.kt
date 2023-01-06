import GameBoard.WINDOW_HEIGHT
import GameBoard.WINDOW_WIDTH
import java.awt.Color
import java.awt.Graphics
import javax.swing.Timer
import kotlin.math.ceil
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

enum class AttackState {
    RUNNING,
    STOPPED
}

abstract class Attack(
    val name: String,
    val description: String,
    var level: Int,
    val damage: Int,
    val cooldownMillisec: Int = 1500
) {
    var frameCount = 0
    var pos = Vector(0.0, 0.0)
    var state: AttackState = AttackState.STOPPED

    fun initAttack(hero: Hero) {
        val stepTimer = Timer(cooldownMillisec) { launchAttack(hero) }
        stepTimer.start()
    }

    fun IntRange.convert(number: Int, target: IntRange): Int {
        val ratio = number.toFloat() / (endInclusive - start)
        return (ratio * (target.last - target.first)).toInt()
    }

    fun calculateDistance(x1: Int, y1: Int, x2: Int, y2: Int): Double {
        return sqrt((x2 - x1).toDouble().pow(2.0) + (y2 - y1).toDouble().pow(2.0))
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
        return (frameCount % 5 == 0 && ceil(hero.pos.dist(enemy.pos)) <= currentSize && state == AttackState.RUNNING)
    }

    override fun launchAttack(hero: Hero) {
        frameCount = 0
        state = AttackState.RUNNING
        // println("started again: $cooldownMillisec")
    }

    override fun draw(hero: Hero, graphics: Graphics) {
        if (state == AttackState.STOPPED) return

        frameCount++
//        currentSize = (sin((0..frameLength).convert(frameCount, (0..Math.PI.toInt())).toDouble()) * 5).toInt().coerceIn(0..size)
        currentSize = (size * sin(((1.0 / 19) * frameCount))).toInt().coerceIn(0..size)
//        println("currentSize: $currentSize ")
        val greenAmount = (0..size).convert(currentSize, (0..200))
        graphics.color = Color(255 - greenAmount, 200, 255 - greenAmount)
        graphics.fillOval(
            WINDOW_WIDTH / 2 - currentSize / 2,
            WINDOW_HEIGHT / 2 - currentSize / 2,
            currentSize,
            currentSize
        )
        graphics.color = Color.BLACK

        if (currentSize <= 1 && frameCount > frameLength) {
            state = AttackState.STOPPED
            // println("stopped: $state")
        }
    }
}

class StaticAttack(name: String, description: String, level: Int, damage: Int, cooldownMillisec: Int) : Attack(
    name, description, level,
    damage, cooldownMillisec
) {
    val size: Int = 128;
    var currentSize = 0

    override fun checkCollisions(enemy: Enemy, hero: Hero): Boolean {
        return (frameCount % 30 == 0 && ceil(pos.dist(enemy.pos)) <= currentSize)
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
        graphics.color = Color(200, 255 - redAmount, 255 - redAmount)
        graphics.fillOval(
            pos.x.toInt() - hero.pos.x.toInt() + WINDOW_WIDTH / 2 - currentSize / 2,
            pos.y.toInt() - hero.pos.y.toInt() + WINDOW_HEIGHT / 2 - currentSize / 2,
            currentSize,
            currentSize
        )
        graphics.color = Color.BLACK
    }
}