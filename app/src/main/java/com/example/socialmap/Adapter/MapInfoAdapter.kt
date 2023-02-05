package com.example.socialmap.Adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.core.view.isInvisible
import com.example.socialmap.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class MapInfoAdapter (context: Context): GoogleMap.InfoWindowAdapter {

    private val contentView_visible = (context as Activity).layoutInflater.inflate(R.layout.map_infoview,null)
    private val contentView_invisible = (context as Activity).layoutInflater.inflate(R.layout.custom_infowindow,null)

    override fun getInfoContents(marker: Marker): View? {

        renderView(marker, contentView_invisible)
        return contentView_invisible

    }

    override fun getInfoWindow(marker: Marker): View? {

        renderView(marker, contentView_invisible)
        return contentView_invisible


    }


    private fun renderView(marker : Marker? , contentView : View){
        val title = marker?.title

        val titletextView = contentView.findViewById<TextView>(R.id.locationMessage)
        titletextView.text = title


    }
}