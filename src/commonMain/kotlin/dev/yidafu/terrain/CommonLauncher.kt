package dev.yidafu.terrain

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.scene.addColorMesh
import de.fabmax.kool.scene.defaultOrbitCamera
import de.fabmax.kool.scene.scene
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.debugOverlay
import dev.yidafu.terrain.core.HeightMap
import dev.yidafu.terrain.kool.addTriangulatedMesh

private fun HeightMap.getVector3f(
    x: Int,
    y: Int,
): Vec3f {
    val z = get(x, y).toFloat() / 256f
    println("Vec3f => x: $x, y: $y, z: $z")
    return Vec3f(x.toFloat(), y.toFloat(), z * 16)
}

/**
 * Main application entry. This demo creates a small example scene, which you probably want to replace by your actual
 * game / application content.
 */
fun launchApp(ctx: KoolContext) {
    // add a hello-world demo scene
    val heightMap = MidpointDisplacement(1.5, 16).generate()
    val stripList = mutableListOf<List<Vec3f>>()
    for (y in 0..<heightMap.size - 1) {
        val vectors = mutableListOf<Vec3f>()
        for (x in 0..<(heightMap.size)) {
            val v1 = heightMap.getVector3f(x, y)
            val v2 = heightMap.getVector3f(x, y + 1)
            vectors.add(v1)
            vectors.add(v2)
        }
        stripList.add(vectors)
    }

    ctx.scenes +=
        scene {
            // enable simple camera mouse control
            defaultOrbitCamera()
            addColorMesh {
                generate {

                    grid {
                        sizeY = 32f
                        sizeX = 32f
                        xDir.set(Vec3f.X_AXIS)
                        yDir.set(Vec3f.NEG_Y_AXIS)
                    }
                }
                shader =
                    KslPbrShader {
                        color { vertexColor() }
                        metallic(0f)
                        roughness(0.25f)
                    }
            }

            stripList.forEachIndexed {idx,list ->
                addTriangulatedMesh(makeChildName("mesh-$idx"), list)
            }

            // set up a single light source
            lighting.singleDirectionalLight {
                setup(Vec3f(1f, 1f, 1f))
                setColor(Color.WHITE, 5f)
            }
        }

    // add the debugOverlay. provides an fps counter and some additional debug info
    ctx.scenes += debugOverlay()
}
