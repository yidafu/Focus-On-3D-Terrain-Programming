package dev.yidafu.terrain.flatiron

import kotlin.math.sqrt

/**
 * apply FIR filter to material
 */
class SimpleFlatiron(
    val filter: Float = 0.3f,
) : Flatiron {
    override fun iron(material: FloatArray) {
        val count = sqrt(material.size.toDouble()).toInt()

        repeat(16) {
            for (i in 0..<count) {
                // top to bottom
                firFilter(material, i, count, count, filter)
                // bottom to top
                firFilter(material, count * count - 1, -count, count, filter)

                // left to right
                firFilter(material, i * count, 1, count, filter)
                // right to left
                firFilter(material, i * count + count - 1, -1, count, filter)
            }

//            var value = material[0]
//            for (j in 0..<count / 2) {
//                val start = j
//                val end = count - j
//                for (m in start..<end) {
//                    val index = j * count + m
//                    material[index] = filter * value + (1 - filter) * material[index]
//                    value = material[index]
//                }
//                for (n in end ..< (count - j * 2))
//            }
        }
    }

    private fun firFilter(
        band: FloatArray,
        start: Int,
        stride: Int,
        count: Int,
        filter: Float,
    ) {
        var value = band[start]
        var i = start
        repeat(count) {
            band[i] = filter * value + (1 - filter) * band[i]
            value = band[i]
            i += stride
        }
    }
}
