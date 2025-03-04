package dev.yidafu.terrain.dev.yidafu.terrain.renderer

import com.jogamp.opengl.GLCapabilities
import com.jogamp.opengl.GLProfile
import com.jogamp.opengl.awt.GLCanvas
import com.jogamp.opengl.util.Animator
import dev.yidafu.terrain.renderer.GLRenderer
import javax.swing.JFrame


class JvmGLRenderer : GLRenderer() {
    override fun render() {
        val glProfile = GLProfile.get(GLProfile.GL3)
        val capabilities = GLCapabilities(glProfile)

        val canvas: GLCanvas = GLCanvas(capabilities)
        val animator = Animator(canvas)
        val surface = CoordinatePanel()
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