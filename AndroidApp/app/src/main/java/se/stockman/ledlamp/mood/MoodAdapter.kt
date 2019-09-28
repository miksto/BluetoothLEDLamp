package se.stockman.ledlamp.mood

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import kotlinx.android.synthetic.main.effect_grid_item.view.*
import se.stockman.ledlamp.R
import se.stockman.ledlamp.data.LampEffect

/**
 * Created by Mikael Stockman on 2019-09-25.
 */

class MoodAdapter : BaseAdapter() {

    private class EffectItem(val id: Int, @StringRes val name: Int, @DrawableRes val icon: Int)

    private var items: List<EffectItem>


    init {
        items = listOf(
            EffectItem(
                LampEffect.sunset,
                R.string.mood_sunset,
                R.drawable.ic_menu_color_blend
            ),
            EffectItem(
                LampEffect.woods,
                R.string.mood_woods,
                R.drawable.ic_menu_color_blend
            ),
            EffectItem(
                LampEffect.sakura,
                R.string.mood_sakura,
                R.drawable.ic_menu_color_blend
            ),
            EffectItem(
                LampEffect.ruby_room,
                R.string.mood_ruby,
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