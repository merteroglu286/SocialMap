package com.example.socialmap.Adapter

import android.content.Context
import com.example.socialmap.LocationModel
import com.google.android.gms.maps.GoogleMap
import nz.co.trademe.mapme.LatLng
import nz.co.trademe.mapme.annotations.AnnotationFactory
import nz.co.trademe.mapme.annotations.MapAnnotation
import nz.co.trademe.mapme.annotations.MarkerAnnotation
import nz.co.trademe.mapme.googlemaps.GoogleMapMeAdapter

class MapsAdapter(context: Context, private val markers: ArrayList<LocationModel>):
    GoogleMapMeAdapter(context){

    override fun onCreateAnnotation(
        factory: AnnotationFactory<GoogleMap>,
        position: Int,
        annotationType: Int
    ): MapAnnotation {
        val item = this.markers[position]
        if (!item.latitude.isEmpty() && !item.longitude.isEmpty()){
            return factory.createMarker(LatLng(item.latitude.toDouble(),item.longitude.toDouble()),null,null)
        }else{
            return factory.createMarker(LatLng(41.013530,28.775297),null,null)
        }

    }

    override fun onBindAnnotation(annotation: MapAnnotation, position: Int, payload: Any?) {
        if (annotation is MarkerAnnotation){
            val item = this.markers[position]
        }
    }

    override fun getItemCount(): Int {
        return markers.size
    }
}