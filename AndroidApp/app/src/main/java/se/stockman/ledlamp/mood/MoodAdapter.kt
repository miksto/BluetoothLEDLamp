package se.stockman.ledlamp.mood

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import kotlinx.android.synthetic.main.effect_grid_item.view.*
import se.stockman.ledlamp.R

/**
 * Created by Mikael Stockman on 2019-09-25.
 */

class MoodAdapter : BaseAdapter() {

    companion object {
        const val sunset = 100
        const val woods = 101
        const val sakura = 102
        const val ruby_room = 103
        const val star_night = 104
        const val sunset2 = 105
        const val timed_sunset = 106
        const val flower_field = 107
        const val fall = 108
        const val clouds_effect = 109
        const val fire_effect = 110
        const val brown_landscape = 111
    }

    private class EffectItem(val id: Int, @StringRes val name: Int, @DrawableRes val icon: Int)

    private var items: List<EffectItem>


    init {
        items = listOf(
            EffectItem(
                sunset,
                R.string.mood_sunset,
                R.drawable.ic_menu_color_blend
            ),
            EffectItem(
                sunset2,
                R.string.mood_sunset2,
                R.drawable.ic_menu_color_blend
            ),
            EffectItem(
                timed_sunset,
                R.string.mood_timed_sunset,
                R.drawable.ic_menu_color_blend
            ),
            EffectItem(
                woods,
                R.string.mood_woods,
                R.drawable.ic_menu_color_blend
            ),
            EffectItem(
                sakura,
                R.string.mood_sakura,
                R.drawable.ic_menu_color_blend
            ),
            EffectItem(
                ruby_room,
                R.string.mood_ruby,
                R.drawable.ic_menu_color_blend
            ),
            EffectItem(
                star_night,
                R.string.mood_star_night,
                R.drawable.ic_menu_color_blend
            ),
            EffectItem(
                flower_field,
                R.string.mood_flower_field,
                R.drawable.ic_menu_color_blend
            ),
            EffectItem(
                fall,
                R.string.mood_fall,
                R.drawable.ic_menu_color_blend
            ),
            EffectItem(
                brown_landscape,
                R.string.mood_brown_landscape,
                R.drawable.ic_menu_color_blend
            ),
            EffectItem(
                clouds_effect,
                R.string.mood_clouds,
                R.drawable.ic_menu_color_blend
            ),
            EffectItem(
                fire_effect,
                R.string.mood_fire,
                R.drawable.ic_menu_color_blend
            )
        )
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        if (convertView == null) {
            view = LayoutInflater.from(parent?.context)
                .inflate(R.layout.effect_grid_item, parent, false)
        } else {
            view = convertView
        }
        val item = items[position]
        val name = parent?.context?.getString(item.name)

        view.effect_name.text = name
        view.effect_icon.setImageResource(item.icon)
        return view
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return items[position].id.toLong()
    }

    override fun getCount(): Int {
        return items.size
    }

}