
import de.fabmax.kool.KoolApplication
import de.fabmax.kool.KoolConfigJvm
import de.fabmax.kool.math.Vec2i
import dev.yidafu.terrain.launchApp

/**
 * JVM main function / app entry point: Creates a new KoolContext (with optional platform-specific configuration) and
 * forwards it to the common-code launcher.
 */
fun main() {
    KoolApplication(
        config = KoolConfigJvm(
            renderBackend = KoolConfigJvm.Backend.VULKAN,
            windowTitle = "Kool Demo",
            windowSize = Vec2i(1600, 900)
        )
    ) {
        launchApp(ctx)
    }
}