package com.example.socialmap.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmap.R
import com.example.socialmap.activities.FullscreenPhotoActivity
import com.example.socialmap.activities.MessageActivity
import kotlinx.android.synthetic.main.item_isletme_photos.view.*

class ProfileIsletmePhotosAdapter(val images: List<Int>) :
    RecyclerView.Adapter<ProfileIsletmePhotosAdapter.MyViewHolder>() {
    inner class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context). inflate(R.layout.item_isletme_photos,parent,false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val image = images[position]
        holder.itemView.img_isletme_item.setImageResource(image)

        holder.itemView.img_isletme_item.setOnClickListener {
            val intent = Intent(it.context, FullscreenPhotoActivity::class.java)
            intent.putExtra("img",image)
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }
}