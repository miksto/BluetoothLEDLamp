package se.stockman.ledlamp.data

/**
 * Created by Mikael Stockman on 2019-09-07.
 */
data class HlsColorDataObject(val color: HlsColor) : DataObject {

    companion object {
        fun fromByteArray(byteArray: ByteArray): HlsColorDataObject {
            val lightness = byteArray[0].toUByte().toInt()
            val saturation = byteArray[1].toUByte().toInt()
            val hue = (byteArray[3].toUByte().toInt() shl 8) or byteArray[2].toUByte().toInt()
            return HlsColorDataObject(
                HlsColor(
                    hue,
                    saturation,
                    lightness
                )
            )
        }
    }

    override fun toByteArray(): ByteArray {
        val bytes = ByteArray(4)

        bytes[0] = (color.lightness and 0xFF).toByte()
        bytes[1] = (color.saturation and 0xFF).toByte()
        bytes[2] = (color.hue and 0xFF).toByte()
        bytes[3] = ((color.hue shr 8) and 0xFF).toByte()

        return bytes
    }

}