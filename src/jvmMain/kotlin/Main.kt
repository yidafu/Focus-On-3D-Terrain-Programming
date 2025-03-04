package dev.yidafu.terrain

import dev.yidafu.terrain.core.saveImage
import dev.yidafu.terrain.dev.yidafu.terrain.renderer.JvmGLRenderer

fun main() {
 val heightMap = MidpointDisplacement(1.5, 16).generate()
 heightMap.saveImage("temp.png")
 JvmGLRenderer().render(heightMap)
}