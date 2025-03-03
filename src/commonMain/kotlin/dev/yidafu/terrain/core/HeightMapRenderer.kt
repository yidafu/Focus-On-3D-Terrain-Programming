package dev.yidafu.terrain.core

expect class HeightMapRenderer(
    size: Int,
    heightMap: HeightMap,
) {
    fun render()
}
