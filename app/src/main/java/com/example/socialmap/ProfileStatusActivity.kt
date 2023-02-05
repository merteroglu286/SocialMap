package com.example.socialmap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import com.example.socialmap.ViewModels.ProfileViewModel
import com.example.socialmap.databinding.ActivityProfileNameBinding
import com.example.socialmap.databinding.ActivityProfileStatusBinding

class ProfileStatusActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProfileStatusBinding
    private lateinit var profileViewModels: ProfileViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)


        profileViewModels = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(ProfileViewModel::class.java)

        profileViewModels.getUser().observe(this, androidx.lifecycle.Observer { userModel->
            binding.userModel = userModel

            //Picasso.get().load(userModel.image).into(binding.imgProfile)
        })

        binding.editUserStatus.requestFocus()
        binding.editUserStatus.postDelayed(Runnable { binding.editUserStatus.setSelection(binding.editUserStatus.length()) }, 50)
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        binding.button.setOnClickListener {

            val status = binding.editUserStatus.text.toString()
            if (status.isNotEmpty()) {
                profileViewModels.updateStatus(status)
            }
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
            finish()
        }
    }
}