package com.example.socialmap

import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

data class UserModel(
    var name: String = "",
    var firstName: String = "",
    var lastName: String = "",
    val status: String = "",
    val image: String = "",
    var number: String = "",
    val uid: String = "",
    val isletmeAdi : String = "",
    val isletmeTuru : String = "",
    val hesapTipi : Int = 1,
    val kayitliMi : Boolean = false
){


    companion object{
        @JvmStatic
        @BindingAdapter("imageUrl")
        fun loadImage(view:CircleImageView,imageUrl:String?){
            imageUrl?.let {
                Glide.with(view.context).load(imageUrl).into(view)
            }
        }
    }
}
