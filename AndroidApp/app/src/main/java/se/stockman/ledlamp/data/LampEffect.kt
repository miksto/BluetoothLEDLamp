package se.stockman.ledlamp.data

import android.graphics.Bitmap
import se.stockman.ledlamp.effect.EffectAdapter
import se.stockman.ledlamp.mood.MoodAdapter

/**
 * Created by Mikael Stockman on 2019-09-25.
 */
data class LampEffect(val effectId: Int, val data: DataObject?) : DataObject {

    override fun toByteArray(): ByteArray {
        var bytes = byteArrayOf(effectId.toByte())
        data?.let {
            bytes += it.toByteArray()
        }
        return bytes
    }

    companion object {
        private const val esp_32_static_color = 0
        private const val esp_32_beacon_light = 1
        private const val esp_32_color_loop = 2
        private const val esp_32_rotating_lines = 3
        private const val esp_32_rotating_rainbow = 4
        private const val esp_32_glimmer_effect = 5
        private const val esp_32_timed_sunset = 6
        private const val esp_32_fakka_ur = 7
        private const val esp_32_gradient = 8
        private const val esp_32_patchy_colors_effect = 9
        private const val esp_32_clouds_effect = 10
        private const val esp_32_pixel_control_effect = 11

        fun moodFromId(id: Int): LampEffect {
            return when (id) {
                MoodAdapter.sunset -> createGlimmerEffect(
                    RgbColor(200, 14, 0),
                    RgbColor(33, 0, 1)
                )

                MoodAdapter.sunset2 -> createGlimmerEffect(
                    RgbColor(200, 14, 0),
                    RgbColor(200, 28, 0)
                )

                MoodAdapter.timed_sunset -> LampEffect(
                    esp_32_timed_sunset,
                    SunsetDataObject(
                        timeDuration = 30,
                        colorInterval = 0.025f,
                        startHue = 0.04f,
                        endHue = 0.95f
                    )
                )

                MoodAdapter.woods -> createGlimmerEffect(
                    RgbColor(0, 25, 1),
                    RgbColor(0, 0, 0)
                )

                MoodAdapter.sakura -> createGlimmerEffect(
                    RgbColor(174, 21, 31),
                    RgbColor(80, 60, 120)
                )

                MoodAdapter.ruby_room -> createGlimmerEffect(
                    RgbColor(33, 0, 1),
                    RgbColor(100, 100, 100)
                )

                MoodAdapter.star_night -> createGlimmerEffect(
                    RgbColor(0, 0, 15),
                    RgbColor(5, 5, 80)
                )

                MoodAdapter.flower_field -> createGradientEffect(
                    RgbColor(200, 0, 170),
                    RgbColor(20, 20, 200)
                )

                MoodAdapter.fall -> createPatchyColorsEffect(
                    RgbColor(150, 25, 0),
                    RgbColor(200, 170, 0),
                    RgbColor(80, 200, 0)
                )

                MoodAdapter.brown_landscape -> createGradientEffect(
                    RgbColor(110, 33, 0),
                    RgbColor(110, 53, 0)
                )

                MoodAdapter.clouds_effect -> createCloudsEffect(
                    RgbColor(40, 40, 200),
                    RgbColor(150, 150, 200)
                )

                MoodAdapter.fire_effect -> createCloudsEffect(
                    RgbColor(200, 25, 0),
                    RgbColor(200, 8, 0)
                )

                else -> throw IllegalArgumentException("Not supported mood id")
            }
        }

        fun effectFromId(id: Int): LampEffect {
            return when (id) {
                EffectAdapter.rotating_rainbow -> LampEffect(
                    esp_32_rotating_rainbow,
                    RotatingRainbowDataObject(colorInterval = 0.8f, speed = 0.5f)
                )

                EffectAdapter.flowy_colors -> LampEffect(
                    esp_32_rotating_rainbow,
                    RotatingRainbowDataObject(colorInterval = 0.25f, speed = 0.03f)
                )

                EffectAdapter.fekke -> LampEffect(
                    esp_32_rotating_rainbow,
                    RotatingRainbowDataObject(colorInterval = 0.25f, speed = 1f)
                )

                EffectAdapter.fakka_ur -> LampEffect(esp_32_fakka_ur, null)
                EffectAdapter.beacon_light -> LampEffect(esp_32_beacon_light, null)
                EffectAdapter.color_loop -> LampEffect(esp_32_color_loop, null)
                EffectAdapter.rotating_lines -> LampEffect(esp_32_rotating_lines, null)
                else -> throw IllegalArgumentException("Not supported effect id")
            }
        }

        fun fromBitmap(bitmap: Bitmap): LampEffect {
            return LampEffect(
                esp_32_pixel_control_effect,
                PixelControlDataObject.fromImage(bitmap)
            )
        }

        private fun createCloudsEffect(color1: RgbColor, color2: RgbColor): LampEffect {
            val data = DualRgbColorDataObject(color1, color2)
            return LampEffect(esp_32_clouds_effect, data)
        }

        private fun createPatchyColorsEffect(
            color1: RgbColor,
            color2: RgbColor,
            color3: RgbColor
        ): LampEffect {
            val data = TripleRgbColorDataObject(color1, color2, color3)
            return LampEffect(esp_32_patchy_colors_effect, data)
        }

        private fun createGradientEffect(color1: RgbColor, color2: RgbColor): LampEffect {
            val data = DualRgbColorDataObject(color1, color2)
            return LampEffect(esp_32_gradient, data)
        }

        private fun createGlimmerEffect(primaryColor: RgbColor, secondary: RgbColor): LampEffect {
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