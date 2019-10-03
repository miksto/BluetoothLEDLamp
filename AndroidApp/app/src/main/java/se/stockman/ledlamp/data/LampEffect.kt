package se.stockman.ledlamp.data

import se.stockman.ledlamp.effect.EffectAdapter
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
        const val esp_32_static_color = 0
        const val esp_32_beacon_light = 1
        const val esp_32_color_loop = 2
        const val esp_32_rotating_lines = 3
        const val esp_32_rotating_rainbow = 4
        const val esp_32_glimmer_effect = 5
        const val esp_32_timed_sunset = 6

        fun moodFromId(id: Int): LampEffect {
            return when (id) {
                MoodAdapter.sunset -> createGlimmerEffect(RgbColor(200, 14, 0), RgbColor(33, 0, 1))
                MoodAdapter.sunset2 -> createGlimmerEffect(
                    RgbColor(200, 14, 0),
                    RgbColor(200, 28, 0)
                )
                MoodAdapter.timed_sunset -> LampEffect(
                    esp_32_timed_sunset,
                    SunsetDataObject(
                        timeDuration = 30,
                        colorInterval = 0.025f,
                        startHue = 0.1f,
                        endHue = 0.66f
                    )
                )
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
                else -> throw IllegalArgumentException("Not supported mood id")
            }
        }

        fun effectFromId(id: Int): LampEffect {
            return when (id) {
                EffectAdapter.rotating_rainbow -> LampEffect(
                    esp_32_rotating_rainbow,
                    SimpleValueDataObject.fromFraction(1.0f)
                )
                EffectAdapter.rotating_rainbow2 -> LampEffect(
                    esp_32_rotating_rainbow,
                    SimpleValueDataObject.fromFraction(0.25f)
                )
                EffectAdapter.beacon_light -> LampEffect(esp_32_beacon_light, null)
                EffectAdapter.color_loop -> LampEffect(esp_32_color_loop, null)
                EffectAdapter.rotating_lines -> LampEffect(esp_32_rotating_lines, null)
                else -> throw IllegalArgumentException("Not supported effect id")
            }
        }


        fun createGlimmerEffect(primaryColor: RgbColor, secondary: RgbColor): LampEffect {
            val data = DualRgbColorDataObject(primaryColor, secondary)
            return LampEffect(esp_32_glimmer_effect, data)
        }


        fun createStaticColorEffect(color: RgbColor): LampEffect {
            val data = RgbColorDataObject(color)
            return LampEffect(esp_32_static_color, data)
        }

        fun getColorForStaticColorEffect(bytes: ByteArray): RgbColor {
            val data =
                RgbColorDataObject.fromByteArray(bytes.slice(1 until bytes.size).toByteArray())
            return data.color
        }
    }
}