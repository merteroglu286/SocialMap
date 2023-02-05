package com.example.socialmap.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.socialmap.R
import com.example.socialmap.databinding.ActivityFullscreenPhotoBinding
import com.github.chrisbanes.photoview.PhotoView


class FullscreenPhotoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFullscreenPhotoBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFullscreenPhotoBinding.inflate(layoutInflater)

        val img = intent.getIntExtra("img",0)
        binding.imgFullscreenPhoto.setImageResource(img)
        setContentView(binding.root)
    }
}