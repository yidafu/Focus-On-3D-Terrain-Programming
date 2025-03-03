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
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.charset.StandardCharsets
import java.util.Arrays
import kotlin.math.tan

class TriangleSurface :
    GLEventListener,
    KeyListener {
    private var shaderProgram: Int = 0
    private var vbo: Int = 0
    private var vao: Int = 0
    private val vertices: List<Float> =
        listOf(
            -0.5f,
            0.5f,
            0.0f, // v0 左上
            -0.5f,
            -0.5f,
            0.0f, // v1 左下
            -0.2f,
            0.5f,
            0.0f, // v2 中上
            0.1f,
            -0.5f,
            0.0f, // v3 中下
            0.4f,
            0.4f,
            0.0f, // v4 右上
            0.5f,
            -0.7f,
            0.0f, // v5 右下
        )
//       = listOf(
//            -0.5f,
//            -0.5f,
//            0.0f, // 左下
//            0.5f,
//            -0.5f,
//            0.0f, // 右下
//            0.0f,
//            0.5f,
//            0.0f, // 顶部
//        )

    private val vertexShaderSource =
        """
        #version 330 core
        layout (location = 0) in vec3 aPos;
        flat out vec3 FragPos; // 传递顶点位置到片段着色器
        uniform mat4 view;      // 视图矩阵
        uniform mat4 projection; // 投影矩阵
        
        void main() {
           gl_Position = projection * view * vec4(aPos, 1.0);
           FragPos = aPos; // 直接传递原始顶点坐标
        }
        """.trimIndent()

    private val fragmentShaderSource =
        """
        #version 330 core
        flat in vec3 FragPos;    // 来自顶点着色器的插值位置
        out vec4 FragColor; // 最终颜色输出
        void main() {
            // 基于归一化位置的颜色混合（范围 [-1,1] 转为 [0,1]）
            float r = (FragPos.x + 1.0) * 0.5; // X轴：红
            float g = (FragPos.y + 1.0) * 0.5; // Y轴：绿
            float b = 0.5;                     // 固定蓝
            FragColor = vec4(r,  0.5f, 0.2f, 1.0);
//            FragColor = vec4(1.0f, 0.5f, 0.2f, 1.0f);
       }
        """.trimIndent()
    var fov: Float = 45.0f
    var aspect: Float = (800 / 600).toFloat()
    var near: Float = 0.1f
    var far: Float = 100.0f

    override fun init(drawable: GLAutoDrawable?) {
        val gl = drawable!!.gl.gL3

        // 创建并绑定VAO
        val vaoArray = IntArray(1)
        gl.glGenVertexArrays(1, vaoArray, 0)
        vao = vaoArray[0]
        gl.glBindVertexArray(vao)

        // 创建并绑定VBO
        val vboArray = IntArray(1)
        gl.glGenBuffers(1, vboArray, 0)
        vbo = vboArray[0]
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo)

        // 将顶点数据复制到缓冲区
        val vertexBuffer: FloatBuffer = GLBuffers.newDirectFloatBuffer(vertices.toFloatArray())
        gl.glBufferData(
            GL3.GL_ARRAY_BUFFER,
            (vertices.size * java.lang.Float.BYTES).toLong(),
            vertexBuffer,
            GL3.GL_STATIC_DRAW,
        )

        // 编译着色器
        val vertexShader: Int = compileShader(gl, GL3.GL_VERTEX_SHADER, vertexShaderSource)
        val fragmentShader: Int = compileShader(gl, GL3.GL_FRAGMENT_SHADER, fragmentShaderSource)

        // 创建着色器程序
        shaderProgram = gl.glCreateProgram()
        gl.glAttachShader(shaderProgram, vertexShader)
        gl.glAttachShader(shaderProgram, fragmentShader)
        gl.glLinkProgram(shaderProgram)

        // 设置顶点属性指针
        gl.glVertexAttribPointer(0, 3, GL3.GL_FLOAT, false, 0, 0)
        gl.glEnableVertexAttribArray(0)

        // 解绑
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0)
        gl.glBindVertexArray(0)

        viewMatrixLoc = gl.glGetUniformLocation(shaderProgram, "view");

        val projectionMatrix = FloatArray(16)
        MatrixUtils.perspectiveM(projectionMatrix, 0, fov, aspect, near, far)

        val ¬ = gl.glGetUniformLocation(shaderProgram, "projection")
        gl.glUniformMatrix4fv(projMatrixLoc, 1, false, projectionMatrix, 0)
    }

    private fun compileShader(
        gl: GL3,
        type: Int,
        source: String,
    ): Int {
        val shader = gl.glCreateShader(type)
        gl.glShaderSource(shader, 1, arrayOf(source), null)
        gl.glCompileShader(shader)

        // 检查编译状态
        val compiled = IntBuffer.allocate(1)
        gl.glGetShaderiv(shader, GL3.GL_COMPILE_STATUS, compiled)
        if (compiled[0] == 0) {
            // 获取日志长度
            val logLength = IntBuffer.allocate(1)
            gl.glGetShaderiv(shader, GL3.GL_INFO_LOG_LENGTH, logLength)

            if (logLength[0] > 0) {
                // 分配直接内存的 ByteBuffer
                val logBuffer: ByteBuffer = ByteBuffer.allocateDirect(logLength[0])
                logBuffer.order(ByteOrder.nativeOrder()) // 设置字节序

                // 获取日志内容
                gl.glGetShaderInfoLog(
                    shader,
                    logLength[0],
                    null, // length 参数设为 null（不接收实际长度）
                    logBuffer,
                )

                // 转换字节数据为字符串
                val logBytes = ByteArray(logLength[0])
                logBuffer.get(logBytes)
                val log = String(logBytes, StandardCharsets.UTF_8).trim { it <= ' ' }
                System.err.println("Shader compile error:\n$log")
            } else {
                System.err.println("Shader failed to compile (no log available)")
            }
        }
        return shader
    }

    override fun dispose(drawable: GLAutoDrawable?) {
        val gl = drawable!!.gl.gL3
        gl.glDeleteVertexArrays(1, intArrayOf(vao), 0)
        gl.glDeleteBuffers(1, intArrayOf(vbo), 0)
        gl.glDeleteProgram(shaderProgram)
    }

    override fun display(drawable: GLAutoDrawable?) {
        val gl = drawable!!.gl.gL3

        gl.glClearColor(0.2f, 0.3f, 0.3f, 1.0f)
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT)

        updateViewMatrix(gl)

        gl.glUseProgram(shaderProgram)
        gl.glBindVertexArray(vao)
        gl.glDrawArrays(GL3.GL_TRIANGLE_STRIP, 0, 6)
    }

    private fun updateViewMatrix(gl: GL3) {

        val cameraX =  (Math.cos(Math.toRadians(yaw)) *  Math.cos(Math.toRadians(pitch)))

        val cameraY =  Math.sin(Math.toRadians(pitch));
        val cameraZ =  (Math.sin(Math.toRadians(yaw)) *  Math.cos(Math.toRadians(pitch)));

        // 创建视图矩阵
        val viewMatrix: Matrix4f = Matrix4f()
            .lookAt(
                Vector3f(0f, 0f, 3f),  // 相机位置
                Vector3f(cameraX.toFloat(), cameraY.toFloat(), cameraZ.toFloat()),  // 观察点
                Vector3f(0f, 1f, 0f) // 上方向
            )


        // 传递到着色器
        val buffer: FloatBuffer = Buffers.newDirectFloatBuffer(16);
        viewMatrix.get(buffer)
        gl.glUniformMatrix4fv(viewMatrixLoc, 1, false, buffer)

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
    }

    private var yaw = -90.0 // 初始偏航角
    private var pitch = 0.0
    private var rotateSpeed = 2.0 // 旋转速度（度/帧）
    private var viewMatrixLoc = 0

    override fun keyTyped(p0: KeyEvent?) {
        val keyCode = p0?.keyCode
        when (keyCode) {
            KeyEvent.VK_LEFT -> {
                yaw -= rotateSpeed; // 左键：向左旋转
            }

            KeyEvent.VK_RIGHT -> {
                yaw += rotateSpeed; // 右键：向右旋转
            }

            KeyEvent.VK_UP -> {
                pitch += rotateSpeed; // 上键：向上抬头
            }

            KeyEvent.VK_DOWN -> {
                pitch -= rotateSpeed; // 下键：向下低头
            }
        }
        // 限制俯仰角范围
        pitch = (-89.0).coerceAtLeast(89.0.coerceAtMost(pitch));
    }

    override fun keyPressed(p0: KeyEvent?) {
    }

    override fun keyReleased(p0: KeyEvent?) {
    }
}


object MatrixUtils {
    /**
     * 生成透视投影矩阵（与 Android Matrix.perspectiveM 兼容）
     * @param m          存储矩阵的数组
     * @param offset     数组起始偏移量
     * @param fovy       垂直视野角度（度数）
     * @param aspect     宽高比（width/height）
     * @param near       近裁剪面距离
     * @param far        远裁剪面距离
     */
    fun perspectiveM(
        m: FloatArray?, offset: Int,
        fovy: Float, aspect: Float, near: Float, far: Float
    ) {
        require(!(m == null || m.size - offset < 16)) { "Invalid matrix array" }
        require(!(aspect == 0f || near == far || near <= 0 || far <= 0)) { "Invalid parameters" }

        val f = (1.0 / tan(Math.toRadians(fovy.toDouble()) / 2.0)).toFloat()
        val rangeInv = 1.0f / (near - far)

        // 重置矩阵
        Arrays.fill(m, offset, offset + 16, 0.0f)

        // 填充矩阵（列主序）
        m[offset + 0] = f / aspect // 第1列第1行
        m[offset + 5] = f // 第2列第2行
        m[offset + 10] = (far + near) * rangeInv // 第3列第3行
        m[offset + 11] = -1.0f // 第4列第3行
        m[offset + 14] = 2.0f * far * near * rangeInv // 第3列第4行
    }
}