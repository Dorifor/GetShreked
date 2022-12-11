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

    fun copy() : Vector {
        return Vector(x, y)
    }

    fun mult(m: Double) {
        x = (x * m).toInt()
        y = (y * m).toInt()
    }

    fun mult(vector: Vector) {
        x *= vector.x
        y *= vector.y
    }

    fun mag() : Double {
        return sqrt((x * x + y * y).toDouble())
    }

    fun normalize() {
        this.mult(1 / this.mag())
    }

    fun dist(vector: Vector) : Double {
        val copy = this.copy()
        copy.sub(vector)
        return copy.mag()
    }

    fun setMag(mag: Double) {
        this.normalize()
        this.mult(mag)
    }
}