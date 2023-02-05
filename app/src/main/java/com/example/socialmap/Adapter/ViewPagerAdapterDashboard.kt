package com.example.socialmap.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmap.R
import kotlinx.android.synthetic.main.item_viewpager_dashboard.view.*

class ViewPagerAdapterDashboard(val images : List<Int>) :RecyclerView.Adapter<ViewPagerAdapterDashboard.ViewPagerViewHolder>(){
    inner class ViewPagerViewHolder(itemView:View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_viewpager_dashboard,parent,false)
        return ViewPagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        val curImage = images[position]
        holder.itemView.imgView.setImageResource(curImage)
    }

    override fun getItemCount(): Int {
        return images.size
    }
}