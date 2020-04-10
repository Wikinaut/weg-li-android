package com.github.weg_li_android

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView


class PhotoAdapter constructor(private val mContext: Context, private val photos:  Array<Int>) : BaseAdapter() {

    override fun getCount(): Int {
        return photos.size
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getItem(position: Int): Int {
        return photos[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val imageView = ImageView(mContext)
        val drawable: Drawable = mContext.resources.getDrawable(getItem(position))
        imageView.setImageDrawable(drawable)
        val layoutParams = imageView.layoutParams
        layoutParams.width = 20
        layoutParams.height = 20

        return imageView
    }
}