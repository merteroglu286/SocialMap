package com.example.socialmap.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.socialmap.R

class KullaniciSozlesmesi : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kullanici_sozlesmesi)
    }

    override fun onDestroy() {
        super.onDestroy()
        startActivity(Intent(this, MainActivity::class.java))
    }
}