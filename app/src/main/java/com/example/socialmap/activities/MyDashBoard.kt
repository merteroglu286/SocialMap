package com.example.socialmap.activities
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.socialmap.databinding.ActivityMyDashBoardBinding

class MyDashBoard : AppCompatActivity() {


    private lateinit var binding: ActivityMyDashBoardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyDashBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

}