package dev.yidafu.terrain.dev.yidafu.terrain.renderer

import com.jogamp.opengl.GLCapabilities
import com.jogamp.opengl.GLProfile
import com.jogamp.opengl.awt.GLCanvas
import com.jogamp.opengl.util.Animator
import dev.yidafu.terrain.core.HeightMap
import dev.yidafu.terrain.renderer.GLRenderer
import org.joml.Vector3f
import javax.swing.JFrame

class JvmGLRenderer : GLRenderer() {
    private fun HeightMap.getVector3f(
        x: Int,
        y: Int,
//        value: Float,
    ): Vector3f {
        val pieces = 2f / (size + 1)
        val coordX = pieces * x - 1
        val coordY = pieces * y - 1
        // height should map to [-1, 1]
        val value = get(x, y).toFloat()
        val height = value / 256.0f
        return Vector3f(coordX, coordY, height)
    }

    private fun List<Vector3f>.toFloatList(): List<Float> {
        val list = mutableListOf<Float>()
        forEach {
            list.add(it.x)
            list.add(it.y)
            list.add(it.z)
        }
        return list
    }

    override fun render(heightMap: HeightMap) {
        // 映射到 -1 ~ 1

//        val vectors = heightMap.gridMap { x, y, value ->
//            heightMap.getVector3f(x, y, value.toFloat())
//        }
        val stripList = mutableListOf<List<Float>>()
        for (y in 0..<heightMap.size - 1) {
            val vectors = mutableListOf<Vector3f>()
            for (x in 0..<(heightMap.size)) {
                val v1 = heightMap.getVector3f(x, y)
                val v2 = heightMap.getVector3f(x, y + 1)
                vectors.add(v1)
                vectors.add(v2)
            }
            stripList.add(vectors.toFloatList())
        }

        val glProfile = GLProfile.get(GLProfile.GL3)
        val capabilities = GLCapabilities(glProfile)

        val canvas = GLCanvas(capabilities)
        val animator = Animator(canvas)
        val surface = CoordinatePanel(stripList)
        canvas.addGLEventListener(surface)
        canvas.addKeyListener(surface)

        val frame = JFrame("JOGL Triangle")
        frame.setSize(800, 600)
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.contentPane.add(canvas)
        frame.isVisible = true

        animator.start()
    }
}
