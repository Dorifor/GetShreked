import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.io.File
import java.time.LocalDateTime
import javax.imageio.ImageIO
import javax.swing.*

object GameBoard : JPanel() {
    var volume: Double = 0.5
    var playerName: String = "Shrok"
    var money: Int = 0
    val characters = mutableListOf<Character>()
    val gameSessions = mutableListOf<GameSession>()
    val powerUps = mutableListOf<PowerUp>()
    val maps = mutableListOf<Map>()

    const val FRAMES_PER_SEC = 60
    const val FRAME_IN_MSEC = 1000 / FRAMES_PER_SEC
    const val WINDOW_WIDTH = 800
    const val WINDOW_HEIGHT = 600

    var PAUSED = false

    init {
        preferredSize = Dimension(WINDOW_WIDTH, WINDOW_HEIGHT)
        background = Color.white
    }

    fun initBoard() {
        addMaps()
        SwingUtilities.invokeLater {
            val frame = JFrame()
            with(frame) {
                defaultCloseOperation = JFrame.EXIT_ON_CLOSE
                title = "Get Shreked - $playerName"
                isResizable = false
                add(this@GameBoard, BorderLayout.CENTER)
                pack()
                setLocationRelativeTo(null)
                isVisible = true
            }

            initGamesession(frame, maps[0])
        }
    }

    private fun initGamesession(frame: JFrame, map: Map) {
        val session = GameSession(LocalDateTime.now(), map)

        frame.contentPane = session

        session.initGame(frame)

        frame.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                when (e.keyCode) {
                    KeyEvent.VK_O -> session.map = if (session.map == maps[0]) maps[1] else maps[0]
                }
            }
        })

        val stepTimer = Timer(FRAME_IN_MSEC) { _: ActionEvent? -> session.stepGame() }
        stepTimer.start()
    }

    private fun addMaps() {
        maps.add(Map("BaseV2", "basic map v2", ImageIO.read(File("src/main/resources/mapx2v2.png")), 1012, false, null))
        maps.add(Map("Base", "basic map", ImageIO.read(File("src/main/resources/mapx2.png")), 1012, false, null))
    }

    fun drawUI() {

    }

    fun startSession() {

    }

}