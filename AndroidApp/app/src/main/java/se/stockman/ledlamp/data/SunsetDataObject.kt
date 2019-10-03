package se.stockman.ledlamp.data

/**
 * Created by Mikael Stockman on 2019-10-03.
 */
class SunsetDataObject(
    val timeDuration: Int,
    val colorInterval: Float,
    val startHue: Float,
    val endHue: Float
) : DataObject {

    override fun toByteArray(): ByteArray {
        val bytes = ByteArray(4)
        bytes[0] = timeDuration.toByte()
        bytes[1] = (colorInterval * 255).toByte()
        bytes[2] = (startHue * 255).toByte()
        bytes[3] = (endHue * 255).toByte()
        return bytes;
    }

}