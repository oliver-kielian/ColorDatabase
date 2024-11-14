package com.example.hw11

import android.database.Cursor
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItemAdapter(private var cursor: Cursor, private val itemAdapterListener: ItemAdapterListener): RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    interface ItemAdapterListener{
        fun markAsFavorite(id: Int, checked: Boolean)
    }


    class ViewHolder(colorView: View, itemAdapterListener: ItemAdapterListener): RecyclerView.ViewHolder(colorView) {

        private val textViewColor : TextView = colorView.findViewById(R.id.textViewColor)
        private val textViewRgb : TextView = colorView.findViewById(R.id.textViewRGB)
        private val favoriteCheck : CheckBox = colorView.findViewById(R.id.checkBoxFavorite)
        private var id = 0
        init{
            favoriteCheck.setOnClickListener{itemAdapterListener.markAsFavorite(id, favoriteCheck.isChecked)}
        }

        fun update (color: Cursor){
            color.moveToPosition(adapterPosition)

            val red = color.getColumnIndex("red")
            val green = color.getColumnIndex("green")
            val blue = color.getColumnIndex("blue")
            val favorite = color.getColumnIndex("favorite")
            val idIndex = color.getColumnIndex("_id")
            id = color.getInt(idIndex)

            textViewColor.setBackgroundColor(Color.rgb(color.getInt(red), color.getInt(green), color.getInt(blue)))
            textViewRgb.text = String.format("%s,%s,%s", color.getString(red), color.getString(green), color.getString(blue))
            if (color.getInt(favorite) == 1) {
                favoriteCheck.isChecked = true
            } else {
                favoriteCheck.isChecked = false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.color_layout, parent, false)
        return ViewHolder(view, itemAdapterListener)
    }

    override fun getItemCount(): Int {
        return cursor.count
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        cursor.moveToPosition(position)
        holder.update(cursor)
    }

    fun updateCursor(cursor: Cursor)
    {
        this.cursor.close()
        this.cursor = cursor
        notifyDataSetChanged()
    }
}