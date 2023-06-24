package se.stockman.ledlamp.data

/**
 * Created by Mikael Stockman on 2019-09-07.
 */
interface DataObject {
    fun toByteArray(): ByteArray
}