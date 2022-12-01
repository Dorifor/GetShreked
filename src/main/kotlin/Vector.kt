data class Vector(var x: Int, var y: Int) {
    fun add(vector: Vector) {
        x += vector.x
        y += vector.y
    }

    fun mult(m: Double) {
        x = (x * m).toInt()
        y = (y * m).toInt()
    }

    fun mult(vector: Vector) {
        x *= vector.x
        y *= vector.y
    }

    fun mag() {

    }
}