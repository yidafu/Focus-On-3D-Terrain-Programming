package dev.yidafu.terrain

internal inline fun assert(
    value: Boolean,
    lazyMessage: () -> String,
) {
    if (!value) {
        val message = lazyMessage()
        throw AssertionError(message)
    }
}
