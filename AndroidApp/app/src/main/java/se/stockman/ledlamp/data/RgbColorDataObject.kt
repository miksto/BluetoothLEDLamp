package se.stockman.ledlamp.data

/**
 * Created by Mikael Stockman on 2019-09-07.
 */
data class RgbColorDataObject(val color: RgbColor) : DataObject {

    companion object {
        fun fromByteArray(byteArray: ByteArray): RgbColorDataObject {
            val red = byteArray[0].toUByte().toInt()
            val green = byteArray[1].toUByte().toInt()
            val blue = byteArray[2].toUByte().toInt()
            return RgbColorDataObject(RgbColor(red, green, blue))
        }
    }

    override fun toByteArray(): ByteArray {
        val bytes = ByteArray(3)

        bytes[0] = (color.red and 0xFF).toByte()
        bytes[1] = (color.green and 0xFF).toByte()
        bytes[2] = (color.blue and 0xFF).toByte()

        return bytes
    }

}