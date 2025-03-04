package dev.yidafu.terrain.renderer

import dev.yidafu.terrain.core.HeightMap

abstract class GLRenderer {
    abstract fun render(heightMap: HeightMap)
}