package se.stockman.ledlamp.data

import se.stockman.ledlamp.mood.MoodAdapter

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
        const val glimmer_effect = 5


        fun moodFromId(id: Int): LampEffect {
            return when (id) {
                MoodAdapter.sunset -> createGlimmerEffect(RgbColor(200, 14, 0), RgbColor(33, 0, 1))
                MoodAdapter.sunset2 -> createGlimmerEffect(RgbColor(200, 14, 0), RgbColor(200, 28, 0))
                MoodAdapter.woods -> createGlimmerEffect(RgbColor(0, 25, 1), RgbColor(0, 0, 0))
                MoodAdapter.sakura -> createGlimmerEffect(
                    RgbColor(174, 21, 31),
                    RgbColor(60, 60, 120)
                )
                MoodAdapter.ruby_room -> createGlimmerEffect(
                    RgbColor(33, 0, 1),
                    RgbColor(100, 100, 100)
                )
                MoodAdapter.star_night -> createGlimmerEffect(
                    RgbColor(0, 0, 15),
                    RgbColor(0, 0, 80)
                )
                else -> createStaticColorEffect(RgbColor(174, 21, 31))
            }
        }

        fun effectFromId(id: Int): LampEffect {
            return LampEffect(id, null)
        }


        fun createGlimmerEffect(primaryColor: RgbColor, secondary: RgbColor): LampEffect {
            val data = DualRgbColorDataObject(primaryColor, secondary)
            return LampEffect(glimmer_effect, data)
        }


        fun createStaticColorEffect(color: RgbColor): LampEffect {
            val data = RgbColorDataObject(color)
            return LampEffect(static_color, data)
        }

        fun getColorForStaticColorEffect(bytes: ByteArray): RgbColor {
            val data =
                RgbColorDataObject.fromByteArray(bytes.slice(1 until bytes.size).toByteArray())
            return data.color
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