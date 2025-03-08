package dev.yidafu.terrain.kool

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Node.Companion.makeNodeName
import de.fabmax.kool.scene.addMesh
import de.fabmax.kool.scene.geometry.PrimitiveType

fun Node.addTriangulatedMesh(
    name: String = makeNodeName("TriangularSurfaceMesh"),
    points: List<Vec3f>,
): Mesh {
    val block: Mesh.() -> Unit = {
        generate {
            withTransform {
                translate(-8f, -8f, -16f)
                triangularSurface(TriangularSurfaceProps(points))
            }

        }
        shader = customShader
    }
    return addMesh(
        Attribute.POSITIONS,
        Attribute.NORMALS,
        Attribute.COLORS,
        name = name,
        primitiveType = PrimitiveType.TRIANGLE_STRIP,
        block = block,
    )
}

