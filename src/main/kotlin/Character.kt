import java.awt.image.BufferedImage

data class Character(val name: String, val description: String, val speed: Double, val skin: BufferedImage, var unlocked: Boolean = false)