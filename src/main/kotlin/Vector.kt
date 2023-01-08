class Vector(var x: Double, var y: Double) {

    // default constructor
    constructor(): this(0.0, 0.0)

    // copy constructor
    constructor(other: Vector): this(other.x, other.y)

    // addition
    operator fun plus(other: Vector) = Vector(x + other.x, y + other.y)

    // subtraction
    operator fun minus(other: Vector) = Vector(x - other.x, y - other.y)

    // multiplication by a scalar
    operator fun times(scalar: Double) = Vector(x * scalar, y * scalar)

    // division by a scalar
    operator fun div(scalar: Double) = Vector(x / scalar, y / scalar)

    // dot product
    fun dot(other: Vector) = x * other.x + y * other.y

    fun distance(other: Vector): Double = (this - other).magnitude()

    // length of the vector
    fun magnitude() = Math.sqrt(x * x + y * y)

    // normalize the vector
    fun normalize() {
        val m = magnitude()
        x /= m
        y /= m
    }

    override fun toString(): String {
        return "($x, $y)"
    }
}
