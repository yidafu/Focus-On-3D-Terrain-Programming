package dev.yidafu.terrain.core

import dev.yidafu.terrain.assert
import kotlin.math.floor
import kotlin.math.sqrt

interface HeightMap {
    val size: Int

    fun get(
        x: Int,
        y: Int,
//        value: UByte,
    ): UByte

    fun get(p: Vertex): UByte

    fun set(
        p: Vertex,
        height: UByte,
    )

    fun set(
        x: Int,
        y: Int,
        height: UByte,
    )
    fun setHeightScale(scale: Float)

    fun getScaled(
        x: Int,
        y: Int,
    ): Float
}

class HeightMapImpl(
    override val size: Int,
    @OptIn(ExperimentalUnsignedTypes::class) val mData: UByteArray = UByteArray(size),
) : HeightMap {
    private var mHeightScale: Float = 1f

    companion object {
        @OptIn(ExperimentalUnsignedTypes::class)
        fun empty() = HeightMapImpl(0)
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun get(
        x: Int,
        y: Int,
//        value: UByte,
    ): UByte = mData.get(x, y)

    override fun get(p: Vertex): UByte = mData.get(p)

    override fun set(
        p: Vertex,
        height: UByte,
    ) = mData.set(p, height)

    override fun set(
        x: Int,
        y: Int,
        height: UByte,
    ) = mData.set(x, y, height)

    override fun setHeightScale(scale: Float) {
        mHeightScale = scale
    }

    override fun getScaled(
        x: Int,
        y: Int,
    ): Float = mData.get(x, y).toFloat() * mHeightScale
}

inline fun HeightMap.grid(cb: (x: Int, y: Int, value: UByte) -> Unit) {
    for (y in 0..<size) {
        for (x in 0..<size) {
            cb(x, y, get(x, y))
        }
    }
}
inline fun <T> HeightMap.gridMap(cb: (x: Int, y: Int, value: UByte) -> T): List<T> {
    val list = mutableListOf<T>()
    for (y in 0..<size) {
        for (x in 0..<size) {
            list.add(cb(x, y, get(x, y)))
        }
    }
    return list
}


@OptIn(ExperimentalUnsignedTypes::class)
val UByteArray.width: Int
    get() {
            val width = sqrt(size.toDouble())
            assert(width >= floor(width)) {
                "UByteArray must be a square"
            }

            return width.toInt()
    }

@OptIn(ExperimentalUnsignedTypes::class)
inline fun UByteArray.set(
    x: Int,
    y: Int,
    height: UByte,
) {
    this[y * width + x] = height
}

@OptIn(ExperimentalUnsignedTypes::class)
inline fun UByteArray.set(
    p: Vertex,
    height: UByte,
) {
    this[p.y * width + p.x] = height
}

@OptIn(ExperimentalUnsignedTypes::class)
inline fun UByteArray.get(
    x: Int,
    y: Int,
): UByte = this[y * width + x]

@OptIn(ExperimentalUnsignedTypes::class)
inline fun UByteArray.get(p: Vertex): UByte = this[p.y * width + p.x]


expect fun HeightMap.saveImage(path: String)