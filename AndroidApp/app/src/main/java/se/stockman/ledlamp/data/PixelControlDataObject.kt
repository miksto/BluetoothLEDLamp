package se.stockman.ledlamp.data

import android.graphics.Bitmap
import android.graphics.Color


/**
 * Created by Mikael Stockman on 2019-10-17.
 */
data class PixelControlDataObject(val colors: List<RgbColor>) : DataObject {

    companion object {
        fun fromImage(bitmap: Bitmap): PixelControlDataObject {
            val strideX = bitmap.width / 7
            val strideY = bitmap.height / 18

            val pixels = List(121) { i ->
                val y = (i / 7) * strideY
                val x = (i % 7) * strideX
                val floats = FloatArray(3)

                Color.colorToHSV(bitmap.getPixel(x, y), floats)
                floats[1] = minOf(floats[1] * 3f, 1f)
                val pixel = Color.HSVToColor(1, floats)

                RgbColor(
                    Color.red(pixel),
                    Color.green(pixel),
                    Color.blue(pixel)
                )
            }
            return PixelControlDataObject(pixels.reversed())
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