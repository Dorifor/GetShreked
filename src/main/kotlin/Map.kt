import java.awt.image.BufferedImage

data class Map(val name: String, val description: String, val image: BufferedImage, var unlocked: Boolean = false)