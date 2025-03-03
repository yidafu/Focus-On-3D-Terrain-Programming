package dev.yidafu.terrain.core

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

    fun getScaled(
        x: Int,
        y: Int,
    ): Float
}

class HeightMapImpl(
    override val size: Int,
    @OptIn(ExperimentalUnsignedTypes::class) val mData: UByteArray = UByteArray(size),
) : HeightMap {
    var heightScale: Float = 1f

    companion object {
        fun empty() = HeightMapImpl(0)
    }

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

    override fun getScaled(
        x: Int,
        y: Int,
    ): Float = mData.get(x, y).toFloat() * heightScale
}

inline fun HeightMap.grid(cb: (x: Int, y: Int, value: UByte) -> Unit) {
    for (y in 0..<size) {
        for (x in 0..<size) {
            cb(x, y, get(x, y))
        }
    }
}

@OptIn(ExperimentalUnsignedTypes::class)
inline fun UByteArray.set(
    x: Int,
    y: Int,
    height: UByte,
) {
    this[y * size + x] = height
}

@OptIn(ExperimentalUnsignedTypes::class)
inline fun UByteArray.set(
    p: Vertex,
    height: UByte,
) {
    this[p.y * size + p.x] = height
}

@OptIn(ExperimentalUnsignedTypes::class)
inline fun UByteArray.get(
    x: Int,
    y: Int,
): UByte = this[y * size + x]

@OptIn(ExperimentalUnsignedTypes::class)
inline fun UByteArray.get(p: Vertex): UByte = this[p.y * size + p.x]
