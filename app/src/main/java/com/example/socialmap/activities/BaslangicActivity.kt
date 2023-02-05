package com.example.socialmap.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.socialmap.R
import com.example.socialmap.databinding.ActivityBaslangicBinding
import com.example.socialmap.databinding.ActivityDashBoardBinding

class BaslangicActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBaslangicBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBaslangicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.text.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}