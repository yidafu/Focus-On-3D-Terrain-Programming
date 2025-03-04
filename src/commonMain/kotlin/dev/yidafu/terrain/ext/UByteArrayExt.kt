package dev.yidafu.terrain.ext

import kotlin.jvm.JvmName
import kotlin.math.sqrt

@OptIn(ExperimentalUnsignedTypes::class)
@JvmName("UByteArray_displayMatrix")
fun UByteArray.displayMatrix() {
    val size = sqrt(this.size.toDouble()).toInt()
    println(" ".repeat(6) + (0..<size).joinToString("") { it.toString().padStart(6, ' ') })
    for (z in 0..<size) {
        print("$z".padStart(6, ' '))
        for (x in 0..<size) {
            print(this[z * size + x].toString().padStart(6, ' '))
        }
        println()
    }
}

@OptIn(ExperimentalUnsignedTypes::class)
@JvmName("UByteArray_grid")
inline fun UByteArray.grid(crossinline callback: (x: Int, y: Int, value: UByte) -> Unit) {
    val size = sqrt(this.size.toDouble()).toInt()
    for (x in 0..<size) {
        for (z in 0..<size) {
            val value = this[z * size + x]
            callback(x, z, value)
        }
    }
}

@OptIn(ExperimentalUnsignedTypes::class)
@JvmName("UByteArray_toJson")
inline fun UByteArray.toJson(): String {
    val list: MutableList<String> = mutableListOf()
    grid { x, y, v ->
        list.add("[$x,$y,$v]")
    }
    return "[" + list.joinToString(",") + "]"
}
