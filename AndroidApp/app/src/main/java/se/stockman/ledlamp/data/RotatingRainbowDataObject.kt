package se.stockman.ledlamp.data

/**
 * Created by Mikael Stockman on 2019-10-03.
 */
data class RotatingRainbowDataObject(
    val colorInterval: Float,
    val speed: Float
) : DataObject {

    override fun toByteArray(): ByteArray {
        val bytes = ByteArray(2)
        bytes[0] = (colorInterval * 255).toInt().toByte()
        bytes[1] = (speed * 255).toInt().toByte()
        return bytes
    }

}