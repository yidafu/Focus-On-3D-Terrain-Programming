package dev.yidafu.terrain

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.util.Color

class TriangularSurfaceProps {
    var triangles = mutableListOf<Vec3f>()
}

fun MeshBuilder.triangularSurface(props: TriangularSurfaceProps) {
    check(props.triangles.size > 3) {
        "triangular surface vertex count must >= 4"
    }
    val indices =
        props.triangles.map {
            vertex(it, Vec3f.Z_AXIS).apply {
                color = Color(it.z, it.z, it.z)
            }
        }
    geometry.addIndices(indices)
}
