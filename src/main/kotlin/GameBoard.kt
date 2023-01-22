import java.awt.*
import java.awt.event.*
import java.awt.image.BufferedImage
import java.io.File
import java.time.LocalDateTime
import javax.imageio.ImageIO
import javax.swing.*
import kotlin.system.exitProcess

object GameBoard : JPanel() {
    var volume: Double = 0.5
    var playerName: String = "Shrok"
    var money: Int = 0
    val characters = mutableListOf<Character>()
    val gameSessions = mutableListOf<GameSession>()
    val powerUps = mutableListOf<PowerUp>()
    val maps = mutableListOf<Map>()
    var currentMapLimit = 0

    var gamepad: Gamepad? = null
    var mousePos: Point = Point()
    var mouseClicked: Boolean = false
    var mouseReleased: Boolean = false

    const val FRAMES_PER_SEC = 60
    const val FRAME_IN_MSEC = 1000 / FRAMES_PER_SEC
    const val WINDOW_WIDTH = 800
    const val WINDOW_HEIGHT = 600

    private val ravieFontResource = Font.createFont(Font.TRUETYPE_FONT, File("src/main/resources/RAVIE.TTF"))
    private val bauhsFontResource = Font.createFont(Font.TRUETYPE_FONT, File("src/main/resources/BAUHS93.TTF"))
    val creamyGardenFontResource = Font.createFont(Font.TRUETYPE_FONT, File("src/main/resources/creamy.ttf"))
    private val deathScreenRessource: BufferedImage = ImageIO.read(File("src/main/resources/death.png"))
    private val startScreenRessource: BufferedImage = ImageIO.read(File("src/main/resources/shrok_bg.jpg"))

    lateinit var frame: JFrame
    lateinit var timer: Timer

    init {
        preferredSize = Dimension(WINDOW_WIDTH, WINDOW_HEIGHT)
        background = Color.white

        gamepad = Gamepad()
        gamepad?.init()
    }

    fun initBoard() {
        addMaps()
        SwingUtilities.invokeLater {
            frame = JFrame()
            with(frame) {
                defaultCloseOperation = JFrame.EXIT_ON_CLOSE
                title = "Get Shreked - $playerName"
                isResizable = false
                add(this@GameBoard, BorderLayout.CENTER)
                pack()
                setLocationRelativeTo(null)
                isVisible = true
                font = bauhsFontResource
            }

            initMainMenu()
        }
    }

    fun initMainMenu() {
        val buttons = mapOf("Start" to {
            timer.stop()
            initGameSession(maps[0])
        }, "Quit" to { exitProcess(0) })

        val startPanel = MenuPanel(buttons, startScreenRessource)
        startPanel.init()
    }

    fun initDeathMenu() {
        money += gameSessions.last().hero.coins

        val buttons = mapOf("Restart" to {
            timer.stop()
            initGameSession(maps[0])
        }, "Stats" to {
            timer.stop()
            initStatsMenu(gameSessions.last())
        }, "Main Menu" to {
            timer.stop()
            initMainMenu()
        }, "Quit" to { exitProcess(0) })

        val deathPanel = MenuPanel(buttons, deathScreenRessource)
        deathPanel.init()
    }

    fun initStatsMenu(session: GameSession) {
        val statsPanel = StatsPanel(session, deathScreenRessource)
        statsPanel.init()
    }

    fun initGameSession(map: Map) {
        val session = GameSession(LocalDateTime.now(), map)
        gameSessions.add(session)

        frame.contentPane = session

        session.initGame()

        frame.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                when (e.keyCode) {
                    KeyEvent.VK_O -> session.map = if (session.map == maps[0]) maps[1] else maps[0]
                    KeyEvent.VK_F -> frame.font =
                        if (frame.font == ravieFontResource) bauhsFontResource else ravieFontResource
                }
            }
        })

        timer = Timer(FRAME_IN_MSEC) { _: ActionEvent? ->
            run {
                session.stepGame()
            }
        }
        timer.start()
    }

    private fun addMaps() {
        maps.add(Map("BaseV2", "basic map v2", ImageIO.read(File("src/main/resources/mapx2v2.png")), 1012, false, null))
        maps.add(Map("Base", "basic map", ImageIO.read(File("src/main/resources/mapx2.png")), 1012, false, null))
    }

    fun IntRange.convert(number: Int, target: IntRange): Int {
        val ratio = number.toFloat() / (endInclusive - start)
        return (ratio * (target.last - target.first)).toInt()
    }
}