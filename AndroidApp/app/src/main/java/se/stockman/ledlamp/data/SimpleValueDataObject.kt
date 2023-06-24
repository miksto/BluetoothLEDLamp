package se.stockman.ledlamp.data

/**
 * Created by Mikael Stockman on 2019-10-03.
 */
data class SimpleValueDataObject(val value: Int) : DataObject {
    companion object {
        fun fromFraction(value: Float): SimpleValueDataObject {
            return SimpleValueDataObject((value * 255).toInt())
        }
    }

    override fun toByteArray(): ByteArray {
        return byteArrayOf(value.toByte())
    }
}