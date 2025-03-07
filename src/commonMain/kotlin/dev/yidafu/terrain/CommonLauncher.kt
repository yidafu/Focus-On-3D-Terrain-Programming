package dev.yidafu.terrain

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.scene.addColorMesh
import de.fabmax.kool.scene.defaultOrbitCamera
import de.fabmax.kool.scene.geometry.PrimitiveType
import de.fabmax.kool.scene.scene
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.debugOverlay
import dev.yidafu.terrain.core.HeightMap

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
                    cube {
                        colored()
                    }
//                    grid {
//                        sizeY = 32f
//                        sizeX = 32f
//                        color = Color.RED
//                    }
//                    grid {
//                        sizeY = 32f
//                        sizeX = 32f
//                        color = Color.BLUE
//                        xDir.set(Vec3f.Y_AXIS)
//                    }

//                    grid {
//                        sizeY = 32f
//                        sizeX = 32f
//                        color = Color.GREEN
//                        texCoordOffset.set(32f, 16f)
//                        xDir.set(Vec3f.NEG_X_AXIS)
//                        yDir.set(Vec3f.NEG_Y_AXIS)
//                    }
                }
                shader =
                    KslPbrShader {
                        color { vertexColor() }
                        metallic(0f)
                        roughness(0.25f)
                    }
            }
            stripList.forEach {
                addColorMesh(
                    name = "colorMesh2",
                    primitiveType = PrimitiveType.TRIANGLE_STRIP,
                ) {
                    generate {
                        triangularSurface(
                            TriangularSurfaceProps().apply {
                                triangles.addAll(it)
                            },
                        )
                    }
                    shader =
                        KslPbrShader {
                            color { vertexColor() }
                            metallic(0f)
                            roughness(0.25f)
                        }
                }

            }

            // set up a single light source
            lighting.singleDirectionalLight {
                setup(Vec3f(-1f, -1f, -1f))
                setColor(Color.WHITE, 5f)
            }
        }

    // add the debugOverlay. provides an fps counter and some additional debug info
    ctx.scenes += debugOverlay()
}
