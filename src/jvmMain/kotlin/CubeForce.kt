package dev.yidafu.terrain

import com.jogamp.opengl.GL2
import com.jogamp.opengl.GLAutoDrawable
import com.jogamp.opengl.GLCapabilities
import com.jogamp.opengl.GLEventListener
import com.jogamp.opengl.GLProfile
import com.jogamp.opengl.awt.GLCanvas
import com.jogamp.opengl.glu.GLU
import dev.yidafu.terrain.core.HeightMap
import dev.yidafu.terrain.core.HeightMapImpl
import javax.swing.JFrame

class CubeForceRender(
    val terrain: Terrain,
) : GLEventListener {
    var rtri: Float = 0.0f

    private val glu = GLU()

    override fun init(drawable: GLAutoDrawable?) {
        requireNotNull(drawable)
        val gl = drawable.gl.gL2

        gl.glClearColor(0f, 0f, 0f, 0f)
        gl.glDisable(GL2.GL_TEXTURE_2D)
        gl.glDisable(GL2.GL_LIGHTING)
        gl.glDisable(GL2.GL_BLEND)
        gl.glEnable(GL2.GL_DEPTH_TEST)

        gl.glShadeModel(GL2.GL_SMOOTH)
        gl.glClearDepth(1.0)
        gl.glDepthFunc(GL2.GL_LEQUAL)
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST)
//        glu.gluLookAt()
//        glu.gluPerspective(60.0, 4.0 / 3.0, 1.0, 100.0)
    }

    override fun dispose(drawable: GLAutoDrawable?) {
    }

    override fun display(drawable: GLAutoDrawable?) {
        requireNotNull(drawable)
        val gl = drawable.gl.gL2

        // Clear The Screen And The Depth Buffer
//        gl.glClear(GL2.GL_COLOR_BUFFER_BIT or GL2.GL_DEPTH_BUFFER_BIT)
//        gl.glLoadIdentity() // Reset The View
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT or GL2.GL_DEPTH_BUFFER_BIT)
        gl.glLoadIdentity() // Reset The View
        gl.glTranslatef(-0.5f, 0.0f, -6.0f) // Move the triangle
//        gl.glRotatef(0f, 0.0f, 1.0f, 0.0f)

        gl.glEnable(GL2.GL_CULL_FACE)
        for (z in 0..<(terrain.size - 1)) {
            gl.glBegin(GL2.GL_TRIANGLE_STRIP)
            for (x in 0..<(terrain.size - 1)) {
//                val uByteColor = terrain.get(x, z).toByte()
//                gl.glColor3ub(uByteColor, uByteColor, uByteColor)
//                gl.glVertex3f(x.toFloat(), terrain.getScaleHeightAtPoint(x, z), z.toFloat())
//
//                val uByteColor2 = terrain.getHeightAtPoint(x, z + 1).toByte()
////                println("color => $uByteColor color 2 => $uByteColor2")
//                gl.glColor3ub(uByteColor2, uByteColor2, uByteColor2)
//                gl.glVertex3f(x.toFloat(), terrain.getScaleHeightAtPoint(x, z + 1), z + 1.0f)
            }
            gl.glEnd() // Done Drawing 3d triangle (Pyramid)
        }

        gl.glBegin(GL2.GL_TRIANGLES)

        // drawing triangle in all dimensions
        // Front
        gl.glColor3f(1.0f, 0.0f, 0.0f); // Red
        gl.glVertex3f(0.0f, 0.0f, 0.0f); // Top Of Triangle (Front)

        gl.glColor3f(0.0f, 1.0f, 0.0f); // Green
        gl.glVertex3f(1f, 1f, 1.0f); // Left Of Triangle (Front)

        gl.glColor3f(0.0f, 0.0f, 1.0f); // Blue
        gl.glVertex3f(0.0f, 1.0f, 1.0f); // Right Of Triangle (Front)
//
//        // Right
//        gl.glColor3f(1.0f, 0.0f, 0.0f); // Red
//        gl.glVertex3f(1.0f, 2.0f, 0.0f); // Top Of Triangle (Right)
//
//        gl.glColor3f(0.0f, 0.0f, 1.0f); // Blue
//        gl.glVertex3f(1.0f, -1.0f, 1.0f); // Left Of Triangle (Right)
//
//        gl.glColor3f(0.0f, 1.0f, 0.0f); // Green
//        gl.glVertex3f(1.0f, -1.0f, -1.0f); // Right Of Triangle (Right)
//
//        // Left
//        gl.glColor3f(1.0f, 0.0f, 0.0f); // Red
//        gl.glVertex3f(1.0f, 2.0f, 0.0f); // Top Of Triangle (Back)
//
//        gl.glColor3f(0.0f, 1.0f, 0.0f); // Green
//        gl.glVertex3f(1.0f, -1.0f, -1.0f); // Left Of Triangle (Back)
//
//        gl.glColor3f(0.0f, 0.0f, 1.0f); // Blue
//        gl.glVertex3f(-1.0f, -1.0f, -1.0f); // Right Of Triangle (Back)
//
//        // left
//        gl.glColor3f(0.0f, 1.0f, 0.0f); // Red
//        gl.glVertex3f(1.0f, 2.0f, 0.0f); // Top Of Triangle (Left)
//
//        gl.glColor3f(0.0f, 0.0f, 1.0f); // Blue
//        gl.glVertex3f(-1.0f, -1.0f, -1.0f); // Left Of Triangle (Left)
//
//        gl.glColor3f(0.0f, 1.0f, 0.0f); // Green
//        gl.glVertex3f(-1.0f, -1.0f, 1.0f); // Right Of Triangle (Left)

        gl.glEnd(); // Done Drawing 3d triangle (Pyramid)

        gl.glFlush()
        rtri += 0.2f
    }

    override fun reshape(
        drawable: GLAutoDrawable?,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
    ) {
        val gl = drawable!!.gl.gL2
        val newHeight = if (height <= 0) 1 else height

        val h = width.toDouble() / newHeight.toDouble()
        gl.glViewport(0, 0, width, height)
        gl.glMatrixMode(GL2.GL_PROJECTION)
        gl.glLoadIdentity()

        glu.gluPerspective(60.0, h, 5.0, 10.0)
        gl.glMatrixMode(GL2.GL_MODELVIEW)
        gl.glLoadIdentity()
        glu.gluLookAt(64f, 0f, 64.0f, 0f, 0f, 0f, 0f, 1f, 0f)
    }
}

abstract class CubeForce(size: Int) : Terrain(size) {
    @OptIn(ExperimentalUnsignedTypes::class)
    override fun generate(): HeightMap {
        val canvas = GLCanvas(GLCapabilities(GLProfile.get(GLProfile.GL2)))
        canvas.addGLEventListener(CubeForceRender(this))
        canvas.setSize(600, 600)

        val frame = JFrame("3d Triangle")
        frame.contentPane.add(canvas)
        frame.size = frame.contentPane.preferredSize
        frame.isVisible = true

//        val animator = FPSAnimator(canvas, 60, true)
//
//        animator.start()
        return HeightMapImpl(size + 1)
    }
}
