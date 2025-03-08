package dev.yidafu.terrain.kool

import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.mvpMatrix
import de.fabmax.kool.modules.ksl.lang.times
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.vertexAttribFloat3
import de.fabmax.kool.pipeline.vertexAttribFloat4

val customShader =
    KslShader("Custom shader") {
        val interStageColor = interStageFloat4()
        vertexStage {
            main {
                val mvp = mvpMatrix()
                val localPosition = float3Var(vertexAttribFloat3(Attribute.POSITIONS))
                outPosition set mvp.matrix * float4Value(localPosition, 1f.const)
                interStageColor.input set vertexAttribFloat4(Attribute.COLORS)
            }
        }
        fragmentStage {

            main {
                colorOutput(interStageColor.output)
            }
        }
    }
