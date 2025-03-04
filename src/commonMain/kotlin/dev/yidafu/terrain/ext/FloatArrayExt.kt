package dev.yidafu.terrain.ext

import dev.yidafu.terrain.assert
import dev.yidafu.terrain.core.Vertex
import kotlin.jvm.JvmName
import kotlin.math.floor
import kotlin.math.sqrt

@OptIn(ExperimentalUnsignedTypes::class)
@JvmName("FloatArray_toUByteArray")
inline fun FloatArray.toUByteArray(): UByteArray {
    val maxValue = this.max()
    val minValue = this.min()
    val range = maxValue - minValue
    val uBytes =
        map {
            floor(((it - minValue) / range * 255).toDouble()).toInt().toUByte()
        }.toUByteArray()
    ubyteArrayOf(*uBytes)
    return uBytes
}

@JvmName("FloatArray_grid")
inline fun FloatArray.grid(crossinline callback: (x: Int, y: Int, value: Float) -> Unit) {
    val size = sqrt(this.size.toDouble()).toInt()
    for (x in 0..<size) {
        for (z in 0..<size) {
            val value = this[z * size + x]
            callback(x, z, value)
        }
    }
}

@JvmName("FloatArray_displayMatrix")
fun FloatArray.displayMatrix() {
    val size = sqrt(this.size.toDouble()).toInt()
    for (x in 0..<size) {
        for (z in 0..<size) {
            print(this[z * size + x].toString().padStart(8, ' '))
        }
        println()
    }
}

val FloatArray.width: Int
    get() {
        val width = sqrt(size.toFloat())
        assert(width > floor(width)) {
            "FloatArray must be a square"
        }
        return width.toInt()
    }

@JvmName("FloatArray_setVertex")
inline fun FloatArray.setVertex(
    vertex: Vertex,
    value: Float,
) {
    val w = width
    this[vertex.y * w + vertex.x] = value
}

@JvmName("FloatArray_getVertex")
inline fun FloatArray.getVertex(vertex: Vertex) = this[vertex.y * width + vertex.x]
