import java.awt.*
import java.awt.event.ActionEvent
import java.io.File
import java.time.LocalDateTime
import javax.imageio.ImageIO
import javax.swing.*

object GameBoard : JPanel() {
    var volume: Double = 0.5
    var playerName: String = "Shrok"
    var money: Int = 0
    val characters = mutableListOf<Character>()

    const val FRAMES_PER_SEC = 60
    const val FRAME_IN_MSEC = 1000 / FRAMES_PER_SEC
    const val WINDOW_WIDTH = 800
    const val WINDOW_HEIGHT = 600

    init {
        preferredSize = Dimension(WINDOW_WIDTH, WINDOW_HEIGHT)
        background = Color.white
    }

    fun initBoard() {
        SwingUtilities.invokeLater {
            val f = JFrame()
            with(f) {
                defaultCloseOperation = JFrame.EXIT_ON_CLOSE
                title = "Get Shreked"
                isResizable = false
                add(this@GameBoard, BorderLayout.CENTER)
                pack()
                setLocationRelativeTo(null)
                isVisible = true
            }

            val m = Map("Base", "basic map", ImageIO.read(File("src/main/resources/mapx2v2.png")))

            val gs = GameSession(LocalDateTime.now(), m)

            f.contentPane = gs

            gs.initGame(f)

            val stepTimer = Timer(FRAME_IN_MSEC) { e: ActionEvent? -> gs.stepGame() }
            stepTimer.start()
        }
    }

    fun drawUI() {

    }

    fun startSession() {

    }

}