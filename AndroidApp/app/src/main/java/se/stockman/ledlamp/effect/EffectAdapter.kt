package se.stockman.ledlamp.effect

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

class EffectAdapter : BaseAdapter() {

    companion object {
        const val beacon_light = 1
        const val color_loop = 2
        const val rotating_lines = 3
        const val rotating_rainbow = 4
        const val flowy_colors = 5
        const val fekke = 6
        const val fakka_ur = 7
        const val pixel_control = 8
    }

    private class EffectItem(val id: Int, @StringRes val name: Int, @DrawableRes val icon: Int)

    private var items: List<EffectItem>


    init {
        items = listOf(
            EffectItem(
                beacon_light,
                R.string.beacon_light,
                R.drawable.ic_menu_color_blend
            ),
            EffectItem(
                color_loop,
                R.string.color_loop,
                R.drawable.ic_menu_color_blend
            ),
            EffectItem(
                rotating_lines,
                R.string.rotating_lines,
                R.drawable.ic_menu_color_blend
            ),
            EffectItem(
                rotating_rainbow,
                R.string.rotating_rainbow,
                R.drawable.ic_menu_color_blend
            ),
            EffectItem(
                flowy_colors,
                R.string.flowy_colors,
                R.drawable.ic_menu_color_blend
            ),
            EffectItem(
                fekke,
                R.string.fekke,
                R.drawable.ic_menu_color_blend
            ),
            EffectItem(
                fakka_ur,
                R.string.fakka_ur,
                R.drawable.ic_menu_color_blend
            ),
            EffectItem(
                pixel_control,
                R.string.pixel_control,
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