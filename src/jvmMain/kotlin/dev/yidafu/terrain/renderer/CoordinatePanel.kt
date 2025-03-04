package dev.yidafu.terrain.dev.yidafu.terrain.renderer

import com.jogamp.common.nio.Buffers
import com.jogamp.opengl.GL3
import com.jogamp.opengl.GLAutoDrawable
import com.jogamp.opengl.GLEventListener
import com.jogamp.opengl.util.GLBuffers
import org.joml.Matrix4f
import org.joml.Vector3f
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.nio.FloatBuffer
import java.nio.IntBuffer
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

class CoordinatePanel(
    private val stripList: List<List<Float>> = listOf(
        listOf(
            -0.5f,
            0.5f,
            0.5f, // v0 左上
            -0.5f,
            -0.5f,
            0.0f, // v1 左下
            -0.2f,
            0.5f,
            -0.7f, // v2 中上
            0.1f,
            -0.5f,
            0.3f, // v3 中下
            0.4f,
            0.4f,
            0.1f, // v4 右上
            0.5f,
            -0.7f,
            0.9f, // v5 右下
        )
    )
) :
    GLEventListener,
    KeyListener {
    private var shaderProgram = 0

    private val vao = IntArray(4)
    private val vbo = IntArray(4)

    private var aspectRatio = 800f / 600f
    private var yaw = -90.0f // 初始偏航角
    private var pitch = 0.0f
    private val rotateSpeed = 2.0f // 旋转速度（度/帧）
    var distance: Float = 3.0f // 相机到原点的距离

    // 平面顶点数据
    val planes =
        arrayOf(
            // X-Y plane (Z=0)
            floatArrayOf(
                -1f,
                -1f,
                0f,
                1f,
                -1f,
                0f,
                1f,
                1f,
                0f,
                -1f,
                -1f,
                0f,
                1f,
                1f,
                0f,
                -1f,
                1f,
                0f,
            ), // Y-Z plane (X=0)
            floatArrayOf(
                0f,
                -1f,
                -1f,
                0f,
                1f,
                -1f,
                0f,
                1f,
                1f,
                0f,
                -1f,
                -1f,
                0f,
                1f,
                1f,
                0f,
                -1f,
                1f,
            ), // X-Z plane (Y=0)
            floatArrayOf(
                -1f,
                0f,
                -1f,
                1f,
                0f,
                -1f,
                1f,
                0f,
                1f,
                -1f,
                0f,
                -1f,
                1f,
                0f,
                1f,
                -1f,
                0f,
                1f,
            ),
        )

    // 编译着色器
    val vertexShaderCode = """#version 330 core
layout(location=0) in vec3 position;
uniform mat4 mvpMatrix;
uniform vec3 color;
out vec3 Color;
void main() {
    gl_Position = mvpMatrix * vec4(position, 1.0);
    Color = color;
}"""

    val fragmentShaderCode = """#version 330 core
in vec3 Color;
out vec4 fragColor;
void main() {
    fragColor = vec4(Color, 0.3);
}"""

    private var shaderProgram2 = 0
    private var stripVbo: IntArray = IntArray(stripList.size)
    private var stripVao: IntArray = IntArray(stripList.size)


    private val vertexShaderSource2 =
        """
        #version 330 core
        layout (location = 0) in vec3 aPos;
        flat out vec3 FragPos; // 传递顶点位置到片段着色器
        uniform mat4 mvpMatrix;

        void main() {
           gl_Position = mvpMatrix * vec4(aPos, 1.0);
           FragPos = aPos; // 直接传递原始顶点坐标
        }
        """.trimIndent()

    private val fragmentShaderSource2 =
        """
         #version 330 core
         flat in vec3 FragPos;    // 来自顶点着色器的插值位置
         out vec4 FragColor; // 最终颜色输出
         void main() {
             // 基于归一化位置的颜色混合（范围 [-1,1] 转为 [0,1]）
             float r = FragPos.z; // X轴：红
             float g = FragPos.z; // Y轴：绿
             float b = FragPos.z;                     // 固定蓝
             FragColor = vec4(r, g, b, 1.0);
        }
        """.trimIndent()

    override fun init(drawable: GLAutoDrawable) {
        val gl = drawable.gl.gL3
        gl.glEnable(GL3.GL_DEPTH_TEST)

        val vertexShader = compileShader(gl, GL3.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = compileShader(gl, GL3.GL_FRAGMENT_SHADER, fragmentShaderCode)

        shaderProgram = gl.glCreateProgram()
        gl.glAttachShader(shaderProgram, vertexShader)
        gl.glAttachShader(shaderProgram, fragmentShader)
        gl.glLinkProgram(shaderProgram)

        val vertexShader2 = compileShader(gl, GL3.GL_VERTEX_SHADER, vertexShaderSource2)
        val fragmentShader2 = compileShader(gl, GL3.GL_FRAGMENT_SHADER, fragmentShaderSource2)

        shaderProgram2 = gl.glCreateProgram()
        gl.glAttachShader(shaderProgram2, vertexShader2)
        gl.glAttachShader(shaderProgram2, fragmentShader2)
        gl.glLinkProgram(shaderProgram2)

        gl.glGenVertexArrays(3, vao, 0)
        gl.glGenBuffers(3, vbo, 0)

        for (i in 0..2) {
            gl.glBindVertexArray(vao[i])
            gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[i])
            val buffer = Buffers.newDirectFloatBuffer(planes[i])
            gl.glBufferData(
                GL3.GL_ARRAY_BUFFER,
                (buffer.limit() * 4).toLong(),
                buffer,
                GL3.GL_STATIC_DRAW,
            )
            gl.glVertexAttribPointer(0, 3, GL3.GL_FLOAT, false, 0, 0)
            gl.glEnableVertexAttribArray(0)
            gl.glBindVertexArray(0)
        }

        // 创建并绑定VAO
        stripVao = IntArray(stripList.size)
        gl.glGenVertexArrays(stripList.size, stripVao, 0)

        // 创建并绑定VBO
        stripVbo = IntArray(stripList.size)
        gl.glGenBuffers(stripList.size, stripVbo, 0)

        for (i in stripList.indices) {
            gl.glBindVertexArray(stripVao[i])
            gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, stripVbo[i])
            // 将顶点数据复制到缓冲区
            val vertexBuffer: FloatBuffer = GLBuffers.newDirectFloatBuffer(stripList[i].toFloatArray())
            gl.glBufferData(
                GL3.GL_ARRAY_BUFFER,
                (stripList[i].size * java.lang.Float.BYTES).toLong(),
                vertexBuffer,
                GL3.GL_STATIC_DRAW,
            )
            // 设置顶点属性指针
            gl.glVertexAttribPointer(0, 3, GL3.GL_FLOAT, false, 0, 0)
            gl.glEnableVertexAttribArray(0)
            gl.glBindVertexArray(0)
        }

        // 解绑
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0)
        gl.glBindVertexArray(0)
    }

    override fun display(drawable: GLAutoDrawable) {
        val gl = drawable.gl.gL3
        gl.glClearColor(0.1f, 0.1f, 0.1f, 1.0f)
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT or GL3.GL_DEPTH_BUFFER_BIT)

        gl.glUseProgram(shaderProgram)

        val mvp = updateViewMatrix()
        val mvpBuffer = Buffers.newDirectFloatBuffer(16)
        mvp.get(mvpBuffer)
        val mvpLoc = gl.glGetUniformLocation(shaderProgram, "mvpMatrix")
        gl.glUniformMatrix4fv(mvpLoc, 1, false, mvpBuffer)

        // 绘制X-Y平面（蓝色）
        gl.glUniform3f(gl.glGetUniformLocation(shaderProgram, "color"), 0f, 0f, 1f)
        gl.glBindVertexArray(vao[0])
        gl.glDrawArrays(GL3.GL_TRIANGLES, 0, 6)

//        // 绘制Y-Z平面（红色）
//        gl.glUniform3f(gl.glGetUniformLocation(shaderProgram, "color"), 1f, 0f, 0f)
//        gl.glBindVertexArray(vao[1])
//        gl.glDrawArrays(GL3.GL_TRIANGLES, 0, 6)

        // 绘制X-Z平面（绿色）
//        gl.glUniform3f(gl.glGetUniformLocation(shaderProgram, "color"), 0f, 1f, 0f)
//        gl.glBindVertexArray(vao[2])
//        gl.glDrawArrays(GL3.GL_TRIANGLES, 0, 6)

        // 绘制三角曲面

        gl.glUseProgram(shaderProgram2)
        val mvpLoc2 = gl.glGetUniformLocation(shaderProgram2, "mvpMatrix")
        gl.glUniformMatrix4fv(mvpLoc2, 1, false, mvpBuffer)

        for (i in stripList.indices) {
            gl.glBindVertexArray(stripVao[i])
            gl.glDrawArrays(GL3.GL_TRIANGLE_STRIP, 0, stripList[i].size / 3)
        }

        gl.glBindVertexArray(0)
        gl.glUseProgram(0)
    }

    override fun reshape(
        drawable: GLAutoDrawable,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
    ) {
        val gl = drawable.gl.gL3
        gl.glViewport(x, y, width, height)
        aspectRatio = width.toFloat() / height
    }

    override fun dispose(drawable: GLAutoDrawable) {
        val gl = drawable.gl.gL3
        gl.glDeleteVertexArrays(3, vao, 0)
        gl.glDeleteBuffers(3, vbo, 0)
        gl.glDeleteProgram(shaderProgram)
    }

    private fun updateViewMatrix(): Matrix4f {
        val radYaw = Math.toRadians(yaw.toDouble()).toFloat()
        val radPitch = Math.toRadians(pitch.toDouble()).toFloat()

        val eyeX: Float = distance * (sin(radYaw.toDouble()) * cos(radPitch.toDouble())).toFloat()
        val eyeY: Float = (distance * sin(radPitch.toDouble())).toFloat()
        val eyeZ: Float = distance * (cos(radYaw.toDouble()) * cos(radPitch.toDouble())).toFloat()
        val eye = Vector3f(eyeX, eyeY, eyeZ)

        // 设置视图矩阵
        val projection: Matrix4f =
            Matrix4f().perspective(Math.toRadians(45.0).toFloat(), aspectRatio, 0.1f, 100.0f)
        val view: Matrix4f =
            Matrix4f()
                .lookAt(
                    eye,
                    Vector3f(0F, 0F, 0F),
                    Vector3f(0F, 0F, 1F),
                )
        val model = Matrix4f()
        val mvp: Matrix4f = projection.mul(view).mul(model)
        return mvp
    }

    private fun compileShader(
        gl: GL3,
        type: Int,
        code: String,
    ): Int {
        val shader = gl.glCreateShader(type)
        val intBuffer = IntBuffer.allocate(1)
        gl.glShaderSource(shader, 1, arrayOf(code), null)
        gl.glCompileShader(shader)
        gl.glGetShaderiv(shader, GL3.GL_COMPILE_STATUS, intBuffer)
        if (intBuffer[0] != GL3.GL_TRUE) {
            System.err.println("Shader compilation error")
            gl.glDeleteShader(shader)
            return 0
        }
        return shader
    }

    override fun keyTyped(p0: KeyEvent?) {
    }

    override fun keyPressed(p0: KeyEvent?) {
        val keyCode = p0?.keyCode
        when (keyCode) {
            KeyEvent.VK_LEFT -> yaw -= rotateSpeed // 左键：向左旋转
            KeyEvent.VK_RIGHT -> yaw += rotateSpeed // 右键：向右旋转
            KeyEvent.VK_UP -> pitch += rotateSpeed // 上键：向上抬头
            KeyEvent.VK_DOWN -> pitch -= rotateSpeed // 下键：向下低头
        }

        // 限制俯仰角范围
        pitch = max(-89.0f, min(89.0f, pitch))
        yaw %= 360.0f; // 保证Yaw在[0, 360)
    }

    override fun keyReleased(p0: KeyEvent?) {
    }
}
