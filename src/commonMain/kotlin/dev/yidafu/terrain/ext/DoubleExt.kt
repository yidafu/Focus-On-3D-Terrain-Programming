package dev.yidafu.terrain.ext

import kotlin.random.Random

internal inline fun Double.randomNext(): Double {
    val value = Random.nextDouble(-this / 2, this / 2)
    return value
}
