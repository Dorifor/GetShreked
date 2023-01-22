import GUI.Menu
import GameBoard.FRAME_IN_MSEC
import GameBoard.frame
import GameBoard.timer
import java.awt.Graphics
import java.awt.event.ActionEvent
import java.awt.image.BufferedImage
import javax.swing.JPanel
import javax.swing.Timer
import kotlin.collections.Map

class MenuPanel(val buttons: Map<String, () -> Unit>, var background: BufferedImage? = null) : JPanel() {
    lateinit var menu: Menu

    fun init() {
        frame.contentPane = this

        revalidate()
        addMouseMotionListener(MouseMotionHandler())
        addMouseListener(MouseHandler())
        menu = Menu()
        menu.generate(buttons)

        timer = Timer(FRAME_IN_MSEC) { _: ActionEvent? ->
            run {
                update()
            }
        }
        timer.start()
    }

    fun update() {
        repaint()
        menu.update()
    }

    override fun paint(graphics: Graphics) {
        if (background != null) graphics.drawImage(background, 0, 0, null)
        menu.draw(graphics)
    }
}