package com.example.socialmap.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmap.CategoryItem
import com.example.socialmap.ProfileIsModel
import com.example.socialmap.R

class MeslekVeEgitimAdapter(private val context: Context, private val categoryItem: List<ProfileIsModel>): RecyclerView.Adapter<MeslekVeEgitimAdapter.CategoryItemViewHolder>() {

    class CategoryItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var itemText : TextView

        init {
            itemText = itemView.findViewById<TextView>(R.id.item_text)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryItemViewHolder {
        return CategoryItemViewHolder(LayoutInflater.from(context).inflate(R.layout.cat_row_item,parent,false))
    }

    override fun onBindViewHolder(holder: CategoryItemViewHolder, position: Int) {
        holder.itemText.text = categoryItem[position].eklenen
    }

    override fun getItemCount(): Int {
        return categoryItem.size
    }

}