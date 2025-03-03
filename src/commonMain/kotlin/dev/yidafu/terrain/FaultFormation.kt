package dev.yidafu.terrain

import dev.yidafu.terrain.core.HeightMap
import dev.yidafu.terrain.core.HeightMapImpl
import dev.yidafu.terrain.core.Vertex
import dev.yidafu.terrain.ext.grid
import dev.yidafu.terrain.ext.toUByteArray
import dev.yidafu.terrain.flatiron.SimpleFlatiron
import kotlin.random.Random

class FaultFormation(
    private val iterations: Int = 32,
    size: Int = 128,
) : Terrain(size) {
    @OptIn(ExperimentalUnsignedTypes::class)
    override fun generate(): HeightMap {
        val tempBuffer = FloatArray(size * size) { 0.0f }
        // 迭代
        (1..iterations).forEach {
            iteration(tempBuffer, it, iterations, 0, 255)
        }

        SimpleFlatiron(0.6f).iron(tempBuffer)
//        heightMap = HeightMapImpl(size, tempBuffer.toUByteArray())

//        HeightMapRenderer(heightMap!!).render()
        return HeightMapImpl(size, tempBuffer.toUByteArray())
    }

    private fun random(): Int = Random.nextInt(size)

    @OptIn(ExperimentalUnsignedTypes::class)
    private fun iteration(
        tempBuffer: FloatArray,
        currentIteration: Int,
        iterations: Int,
        minDelta: Int = 0,
        maxDelta: Int = 255,
    ) {
        // 最开始比较
        val height = maxDelta - ((maxDelta - minDelta) * currentIteration).toFloat() / iterations
        val v1 = Vertex(random(), random())
        val v2 = Vertex(random(), random())

        val direction = v2 - v1
        tempBuffer.toUByteArray().grid { x, z, v ->
            val direction2 = Vertex(x, z) - v1
            if ((direction2 cross direction) > 0) {
                tempBuffer[z * size + x] += height
            }
        }
    }
}
