package com.phamsonhoang.netmapper.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.bumptech.glide.Glide
import com.phamsonhoang.netmapper.R
import com.phamsonhoang.netmapper.models.Example

class OptionsAdapter(
    private val ctx : Context,
    @LayoutRes
    private val layoutRes: Int,
    private val items : List<Example>)
    : ArrayAdapter<Example>(ctx, layoutRes, items) {

    private class ViewHolder {
        lateinit var textView : TextView
        lateinit var imageView : ImageView
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Example? {
        if (position >= items.size) {
            return null
        }
        return items[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup) : View {
        var holder : ViewHolder? = null
        val example = items[position]
        var view = convertView
        if (convertView == null) {
            holder = ViewHolder()
            view = LayoutInflater.from(ctx).inflate(R.layout.list_types_item, parent, false)
            holder.textView = view.findViewById(R.id.optionTxtView)
            holder.imageView = view.findViewById(R.id.optionImageView)
            view.tag = holder
        } else {
            holder = view!!.tag as ViewHolder
        }

        holder.textView.text = example.type
        Glide.with(ctx)
            .load(example.image)
            .placeholder(R.drawable.outline_signal_cellular_alt_black_48)
            .fitCenter()
            .into(holder.imageView)

        return view!!
    }
}