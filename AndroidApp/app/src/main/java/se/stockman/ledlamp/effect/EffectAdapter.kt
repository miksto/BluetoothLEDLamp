package se.stockman.ledlamp.effect

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

class EffectAdapter : BaseAdapter() {

    private class EffectItem(val id: Int, @StringRes val name: Int, @DrawableRes val icon: Int)

    private var items: List<EffectItem>


    init {
        items = listOf(
            EffectItem(
                LampEffect.beacon_light,
                R.string.beacon_light,
                R.drawable.ic_beacon_light
            ),
            EffectItem(
                LampEffect.color_loop,
                R.string.color_loop,
                R.drawable.ic_color_loop
            ),
            EffectItem(
                LampEffect.rotating_lines,
                R.string.rotating_lines,
                R.drawable.ic_color_lines
            ),
            EffectItem(
                LampEffect.rotating_rainbow,
                R.string.rotating_rainbow,
                R.drawable.ic_rainbow
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