package dev.yidafu.terrain.kool

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.util.Color

data class TriangularSurfaceProps(
    var triangles: List<Vec3f> = mutableListOf(),
)

fun MeshBuilder.triangularSurface(props: TriangularSurfaceProps) {
    check(props.triangles.size > 3) {
        "triangular surface vertex count must >= 4"
    }
    val indices =
        props.triangles.map {
            val pos = Vec3f(it.x, it.y, it.z * 2)
            vertex(pos, Vec3f.Z_AXIS).apply {
//                println("color(${it.z / 16}, ${it.z / 16}, ${it.z / 16})")
                color = Color(it.z / 16, it.z / 16, it.z / 16)
            }
        }
    geometry.addIndices(indices)
}
