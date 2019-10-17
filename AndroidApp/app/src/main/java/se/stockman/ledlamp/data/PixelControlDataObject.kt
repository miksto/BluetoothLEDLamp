package se.stockman.ledlamp.data

/**
 * Created by Mikael Stockman on 2019-10-17.
 */
class PixelControlDataObject(val colors: Array<RgbColor>) : DataObject {

    companion object {
        fun createRandomPixelImage(): PixelControlDataObject {
            val pixels = Array(121) { i -> RgbColor(if (i%2==0) 100 else 0, if (i%3==0) 100 else 0, if (i%5==0) 100 else 0) }
            return PixelControlDataObject(pixels)
        }
    }

    override fun toByteArray(): ByteArray {
        val bytes = ByteArray(colors.size * 3 + 1)
        bytes[0] = colors.size.toByte()
        for ((i, color) in colors.withIndex()) {
            val pos = i * 3 + 1
            bytes[pos + 0] = color.red.toByte()
            bytes[pos + 1] = color.green.toByte()
            bytes[pos + 2] = color.blue.toByte()
        }

        return bytes
    }

}