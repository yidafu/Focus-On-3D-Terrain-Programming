package dev.yidafu.terrain.ext

import dev.yidafu.terrain.assert
import dev.yidafu.terrain.core.Vertex
import kotlin.jvm.JvmName
import kotlin.math.floor
import kotlin.math.sqrt

@OptIn(ExperimentalUnsignedTypes::class)
@JvmName("DoubleArray_toUByteArray")
inline fun DoubleArray.toUByteArray(): UByteArray {
    val maxValue = this.max()
    val minValue = this.min()
    val range = maxValue - minValue
    val uBytes =
        map {
            floor(((it - minValue) / range * 100).toDouble()).toInt().toUByte()
        }.toUByteArray()
    ubyteArrayOf(*uBytes)
    return uBytes
}

@JvmName("DoubleArray_displayMatrix")
fun DoubleArray.displayMatrix() {
    val size = sqrt(this.size.toDouble()).toInt()
    for (x in 0..<size) {
        for (z in 0..<size) {
            print(this[z * size + x].toString().padStart(8, ' '))
        }
        println()
    }
}

val DoubleArray.width: Int
    get() {
        val width = sqrt(size.toDouble())
        assert(width >= floor(width)) {
            "DoubleArray must be a square"
        }
        return width.toInt()
    }

@JvmName("DoubleArray_setVertex")
inline fun DoubleArray.setVertex(
    vertex: Vertex,
    value: Double,
) {
    val w = width
    this[vertex.y * w + vertex.x] = value
}

@JvmName("DoubleArray_getVertex")
inline fun DoubleArray.getVertex(vertex: Vertex) = this[vertex.y * width + vertex.x]
