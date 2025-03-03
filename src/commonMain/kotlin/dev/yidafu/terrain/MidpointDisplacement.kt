package dev.yidafu.terrain

import dev.yidafu.terrain.core.HeightMap
import dev.yidafu.terrain.core.HeightMapImpl
import dev.yidafu.terrain.core.Vertex
import dev.yidafu.terrain.ext.getVertex
import dev.yidafu.terrain.ext.randomNext
import dev.yidafu.terrain.ext.setVertex
import dev.yidafu.terrain.ext.toUByteArray
import kotlin.math.pow

/**
 * @url https://stevelosh.com/blog/2016/02/midpoint-displacement/
 * @see explaintion https://craftofcoding.wordpress.com/2021/07/09/midpoint-displacement-in-2d/
 */
class MidpointDisplacement(
    roughness: Double,
    size: Int = 512,
) : Terrain(size) {
    private val roughnessValue = 2.0.pow(-roughness)

    override fun generate(): HeightMap {
//        assert(size % 4 == 0) {
//            "heightmap width($size) must be multiple of 4"
//        }
        val matrix = DoubleArray((size + 1) * (size + 1))
        val topLeft = Vertex(0, 0)
        val topRight = Vertex(size, 0)
        val bottomRight = Vertex(size, size)
        val bottomLeft = Vertex(0, size)
        matrix.setVertex(topLeft, size.toDouble().randomNext())
        matrix.setVertex(topRight, size.toDouble().randomNext())
        matrix.setVertex(bottomRight, size.toDouble().randomNext())
        matrix.setVertex(bottomLeft, size.toDouble().randomNext())

        calculateMidpoint(
            matrix,
            height = size.toDouble(),
            topLeft,
            topRight,
            bottomRight,
            bottomLeft,
        )

        return HeightMapImpl(size + 1, matrix.toUByteArray())
    }

    private fun calculateMidpoint(
        matrix: DoubleArray,
        height: Double,
        topLeft: Vertex,
        topRight: Vertex,
        bottomRight: Vertex,
        bottomLeft: Vertex,
    ) {
        val stepSize = topRight.x - topLeft.x
        if (stepSize == 1) return

        val topMid = (topRight + topLeft) / 2
        matrix.setVertex(topMid, (matrix.getVertex(topLeft) + matrix.getVertex(topRight)) / 2)

        val rightMid = (bottomRight + topRight) / 2
        matrix.setVertex(rightMid, (matrix.getVertex(topRight) + matrix.getVertex(bottomRight)) / 2)

        val bottomMid = (bottomRight + bottomLeft) / 2
        matrix.setVertex(bottomMid, (matrix.getVertex(bottomLeft) + matrix.getVertex(bottomRight)) / 2)

        val leftMid = (bottomLeft + topLeft) / 2
        matrix.setVertex(leftMid, (matrix.getVertex(topLeft) + matrix.getVertex(bottomLeft)) / 2)

        val average =
            (
                matrix.getVertex(topLeft) +
                    matrix.getVertex(topRight) +
                    matrix.getVertex(bottomRight) +
                    matrix.getVertex(bottomLeft)
            ) / 4
        val middle = (bottomRight + topLeft) / 2
        val nextValue = average + height.randomNext() * roughnessValue
        matrix.setVertex(middle, nextValue)

        calculateMidpoint(matrix, height / 2, topLeft, topMid, middle, leftMid)
        calculateMidpoint(matrix, height / 2, topMid, topRight, rightMid, middle)
        calculateMidpoint(matrix, height / 2, middle, rightMid, bottomRight, bottomMid)
        calculateMidpoint(matrix, height / 2, leftMid, middle, bottomMid, bottomLeft)
    }
}
