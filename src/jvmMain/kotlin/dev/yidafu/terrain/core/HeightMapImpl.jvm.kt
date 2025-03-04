package dev.yidafu.terrain.core

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


actual fun HeightMap.saveImage(path: String) {
    val width = size
    val image = BufferedImage(width, width, BufferedImage.TYPE_INT_ARGB)

    for (x in 0..<width) {
        for (y in 0..<width) {
            val value = this.get(x, y).toInt()
            val color = Color(value, value, value).rgb
            image.setRGB(x, y, color)
        }
    }
    val outputFile = File(path)
    ImageIO.write(image, "png", outputFile)
}