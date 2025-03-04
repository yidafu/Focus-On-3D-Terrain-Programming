package dev.yidafu.terrain.ext

import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.math.sqrt

//@OptIn(ExperimentalUnsignedTypes::class)
//@JvmName("JVM_UByteArray_toImage")
//inline fun UByteArray.toImage(): BufferedImage {
//    val width = sqrt(size.toDouble()).toInt()
//    val image = BufferedImage(width, width, BufferedImage.TYPE_INT_ARGB)
//
//    for (x in 0..<width) {
//        for (z in 0..<width) {
//            val value = this[z * width + x].toInt()
//            val color = Color(value, value, value).rgb
//            image.setRGB(x, z, color)
//        }
//    }
//    return image
//}
