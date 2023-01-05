import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.sign
import kotlin.math.sqrt

data class Vector(var x: Int, var y: Int) {
    fun add(vector: Vector) {
        x += vector.x
        y += vector.y
    }

    fun sub(vector: Vector) {
        x -= vector.x
        y -= vector.y
    }

    fun mult(m: Double) {
        val sx = sign(x.toDouble())
        val sy = sign(y.toDouble())
        x = (ceil(abs(x) * m) * sx).toInt()
        y = (ceil(abs(y) * m) * sy).toInt()
    }

    fun mult(vector: Vector) {
        x *= vector.x
        y *= vector.y
    }

    fun normalize() {
        if (this.mag() != 0.0) {
            this.mult(1 / this.mag())
        }
    }

    fun limit(max: Double) {
        if (this.mag() > max) {
            this.setMag(max)
        }
    }

    fun setMag(mag: Double) {
        this.normalize()
        this.mult(mag)
    }

    fun copy() : Vector {
        return Vector(x, y)
    }

    fun mag() : Double {
        return sqrt((x * x + y * y).toDouble())
    }

    fun dist(vector: Vector) : Double {
        val copy = this.copy()
        copy.sub(vector)
        return copy.mag()
    }
}