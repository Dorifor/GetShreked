import GUI.Button
import GameBoard.FRAME_IN_MSEC
import GameBoard.WINDOW_HEIGHT
import GameBoard.WINDOW_WIDTH
import GameBoard.creamyGardenFontResource
import GameBoard.frame
import GameBoard.initMainMenu
import GameBoard.timer
import java.awt.Color
import java.awt.Graphics
import java.awt.Point
import java.awt.event.ActionEvent
import java.awt.image.BufferedImage
import javax.swing.JPanel
import javax.swing.Timer

class StatsPanel(val session: GameSession, var background: BufferedImage? = null) : JPanel() {
    lateinit var button: Button

    val stats = listOf(
        "Map: ${session.map.name}",
        "Enemies Beaten: ${session.enemiesBeaten}",
        "Bosses Beaten: ${session.bossesBeaten}",
        "XP gained: ${session.hero.experience}",
        "Coins Gained: ${session.hero.coins}"
    )


    fun init() {
        frame.contentPane = this

        revalidate()
        addMouseMotionListener(MouseMotionHandler())
        addMouseListener(MouseHandler())

        button = Button(Point(WINDOW_WIDTH / 2, 500), "Menu", 120, 50)

        button.onClick = {
            timer.stop()
            initMainMenu()
        }

        timer = Timer(FRAME_IN_MSEC) { _: ActionEvent? ->
            run {
                update()
            }
        }
        timer.start()
    }

    fun update() {
        repaint()
        button.update()
    }

    override fun paint(graphics: Graphics) {
        graphics.clearRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT)
        if (background != null) graphics.drawImage(background, 0, 0, null)

        graphics.color = Color(0, 0, 0, 63)
        graphics.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT)
        graphics.color = Color(0, 0, 0, 192)
        graphics.fillRect(WINDOW_WIDTH / 2 - 150, WINDOW_HEIGHT / 2 - 63, 300, 200)

        graphics.color = Color.WHITE
        graphics.font = creamyGardenFontResource.deriveFont(32f)
        stats.forEachIndexed { i, s ->
            graphics.drawString(s, WINDOW_WIDTH / 2 - 110, WINDOW_HEIGHT / 2 - 50 + 32 * (i + 1))
        }
        button.draw(graphics)
    }
}