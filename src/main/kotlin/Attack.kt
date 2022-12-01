import GameBoard.WINDOW_HEIGHT
import GameBoard.WINDOW_WIDTH
import java.awt.Color
import java.awt.Graphics
import javax.swing.Timer
import kotlin.math.pow
import kotlin.math.sqrt

abstract class Attack(
    val name: String,
    val description: String,
    var level: Int,
    val damage: Int,
    private val cooldownMillisec: Int = 1500
) {
    var frameCount = 0

    fun initAttack(hero: Hero) {
        val stepTimer = Timer(cooldownMillisec) { launchAttack(hero) }
        stepTimer.start()
    }

    abstract fun checkCollisions(hero: Hero)

    abstract fun launchAttack(hero: Hero)

    abstract fun draw(hero: Hero, graphics: Graphics)
}

class AreaOfEffectAttack(name: String, description: String, level: Int, damage: Int, cooldownMillisec: Int) :
    Attack(name, description, level, damage) {
    val radius: Double = 15.0;
    override fun checkCollisions(hero: Hero) {
        TODO("Not yet implemented")
    }

    override fun launchAttack(hero: Hero) {
        frameCount = 0
    }

    override fun draw(hero: Hero, graphics: Graphics) {
        graphics.drawString("joj", hero.posX, hero.posY)
    }
}

class StaticAttack(name: String, description: String, level: Int, damage: Int, cooldownMillisec: Int) : Attack(
    name, description, level,
    damage, cooldownMillisec
) {
    val size: Int = 64;
    var currentSize = 0
    var posX = 0
    var posY = 0

    fun calculateDistance(x1: Int, y1: Int, x2: Int, y2: Int): Double {
        return sqrt((x2 - x1).toDouble().pow(2.0) + (y2 - y1).toDouble().pow(2.0))
    }

    override fun checkCollisions(hero: Hero) {
        if (frameCount % 30 == 0 && calculateDistance(posX, posY, hero.posX, hero.posY) <= currentSize) {
            hero.health -= damage
        }
    }

    override fun launchAttack(hero: Hero) {
        frameCount = 0
        posX = hero.posX
        posY = hero.posY
    }

    fun IntRange.convert(number: Int, target: IntRange): Int {
        val ratio = number.toFloat() / (endInclusive - start)
        return (ratio * (target.last - target.first)).toInt()
    }

    override fun draw(hero: Hero, graphics: Graphics) {
        frameCount++
        currentSize = (frameCount * 2).coerceIn(0..size)
        val redAmount = (0..size).convert(currentSize, (0..200))
        graphics.color = Color(200, 255 - redAmount, 255 - redAmount)
        // graphics.fillOval(posX - hero.posX + WINDOW_WIDTH / 2, posY - hero.posY + WINDOW_HEIGHT / 2, frame / FRAMES_PER_SEC * 100 * radius, frame / FRAMES_PER_SEC * 100 * radius)
        graphics.fillOval(posX - hero.posX + WINDOW_WIDTH / 2 - currentSize / 2, posY - hero.posY + WINDOW_HEIGHT / 2 - currentSize / 2, currentSize, currentSize)
        graphics.color = Color.BLACK
    }
}