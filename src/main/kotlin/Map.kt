import java.awt.image.BufferedImage

data class Map(val name: String, val description: String, val image: BufferedImage, val limit: Int = 0, var unlocked: Boolean = false, val boss: Character? = null)