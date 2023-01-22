import java.awt.Point
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter

class MouseMotionHandler : MouseMotionAdapter() {
    override fun mouseMoved(e: MouseEvent?) {
        GameBoard.mousePos = e?.point ?: Point()
    }

    override fun mouseDragged(e: MouseEvent?) {
        GameBoard.mousePos = e?.point ?: Point()
    }
}

class MouseHandler : MouseAdapter() {
    override fun mousePressed(e: MouseEvent?) {
        GameBoard.mouseClicked = e?.button == MouseEvent.BUTTON1
    }

    override fun mouseReleased(e: MouseEvent?) {
        GameBoard.mouseClicked = if (e?.button == MouseEvent.BUTTON1) false else GameBoard.mouseClicked
        GameBoard.mouseReleased = e?.button == MouseEvent.BUTTON1
    }
}