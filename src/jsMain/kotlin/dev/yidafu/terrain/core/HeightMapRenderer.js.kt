package dev.yidafu.terrain.core

actual class HeightMapRenderer actual constructor(
    hMap: HeightMapImpl,
) {
    val heightMap: HeightMapImpl = hMap

    actual fun render() {
    }
}
