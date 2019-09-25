package se.stockman.ledlamp.data

/**
 * Created by Mikael Stockman on 2019-09-25.
 */
class LampEffect(val effectId: Int, val data: DataObject?) : DataObject {

    override fun toByteArray(): ByteArray {
        var bytes = byteArrayOf(effectId.toByte())
        data?.let {
            bytes += it.toByteArray()
        }
        return bytes
    }


    companion object {

        const val static_color = 0
        const val beacon_light = 1
        const val color_loop = 2
        const val rotating_lines = 3
        const val rotating_rainbow = 4
        const val sunset = 5
        const val woods = 6
        const val sakura = 7


        fun fromId(id: Int): LampEffect {
            if (id < rotating_rainbow) {
                return LampEffect(id, null)
            }

            return when (id) {
                sunset -> createStaticColorEffect(RgbColor(200, 14, 0))
                woods -> createStaticColorEffect(RgbColor(0, 25, 1))
                sakura -> createStaticColorEffect(RgbColor(174, 21, 31))
                else -> createStaticColorEffect(RgbColor(174, 21, 31))
            }
        }

        fun createStaticColorEffect(color: RgbColor): LampEffect {
            val data = RgbColorDataObject(color)
            return LampEffect(static_color, data)
        }

        fun createColorLoopEffect(): LampEffect {
            return LampEffect(color_loop, null)
        }

        fun createBeaconLightEffect(): LampEffect {
            return LampEffect(beacon_light, null)
        }

        fun createRotatingLinesEffect(): LampEffect {
            return LampEffect(rotating_lines, null)
        }

        fun createRotatingRainbowEffect(): LampEffect {
            return LampEffect(rotating_rainbow, null)
        }
    }
}