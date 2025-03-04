package dev.yidafu.terrain.core

data class Vertex(
    val x: Int,
    val y: Int,
) {
    operator fun minus(other: Vertex): Vertex = Vertex(x - other.x, y - other.y)

    operator fun div(num: Int): Vertex = Vertex(x / num, y / num)

    operator fun plus(other: Vertex): Vertex = Vertex(x + other.x, y + other.y)

    /**
     * 两个首尾相连的向量，通过计算它们的叉积（外积）来判断旋转的方向
     */
    infix fun cross(other: Vertex): Int = x * other.y - other.x * y

    override fun toString(): String = "Vertex {x: $x, y: $y}"
}
