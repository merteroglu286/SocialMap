package com.example.socialmap.activities

import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.socialmap.ViewModels.ProfileViewModel
import com.example.socialmap.databinding.ActivityProfileNameBinding

class ProfileNameActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProfileNameBinding
    private lateinit var profileViewModels: ProfileViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileNameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        profileViewModels = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(ProfileViewModel::class.java)

        profileViewModels.getUser().observe(this, androidx.lifecycle.Observer { userModel->
            binding.userModel = userModel

            //Picasso.get().load(userModel.image).into(binding.imgProfile)
        })

        binding.editUserName.requestFocus()
        binding.editUserName.postDelayed(Runnable { binding.editUserName.setSelection(binding.editUserName.length()) }, 50)
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        binding.button.setOnClickListener {

            val name = binding.editUserName.text.toString()
            if (name.isNotEmpty()) {
                profileViewModels.updateName(name)
            }
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
            finish()
        }




    }
}