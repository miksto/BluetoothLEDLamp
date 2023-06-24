package se.stockman.ledlamp.data

/**
 * Created by Mikael Stockman on 2019-09-07.
 */
data class TripleRgbColorDataObject(
    val color1: RgbColor,
    val color2: RgbColor,
    val color3: RgbColor
) : DataObject {

    override fun toByteArray(): ByteArray {
        val bytes = ByteArray(9)

        bytes[0] = (color1.red and 0xFF).toByte()
        bytes[1] = (color1.green and 0xFF).toByte()
        bytes[2] = (color1.blue and 0xFF).toByte()

        bytes[3] = (color2.red and 0xFF).toByte()
        bytes[4] = (color2.green and 0xFF).toByte()
        bytes[5] = (color2.blue and 0xFF).toByte()

        bytes[6] = (color3.red and 0xFF).toByte()
        bytes[7] = (color3.green and 0xFF).toByte()
        bytes[8] = (color3.blue and 0xFF).toByte()

        return bytes
    }

}