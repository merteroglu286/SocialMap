package com.example.socialmap.fragments

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModelProvider
import com.example.socialmap.AdresModel
import com.example.socialmap.BecerilerModel
import com.example.socialmap.R
import com.example.socialmap.UserModel
import com.example.socialmap.ViewModels.ProfileViewModel
import com.example.socialmap.activities.DashBoard
import com.example.socialmap.databinding.FragmentAdresBilgileriBinding
import com.example.socialmap.databinding.FragmentBeginningBinding
import com.example.socialmap.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AdresBilgileriFragment : Fragment() {

    private lateinit var _binding: FragmentAdresBilgileriBinding
    private val binding get() = _binding!!
    private lateinit var firebaseAuth :FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferences2: SharedPreferences
    private lateinit var profileViewModels: ProfileViewModel
    private var databaseReference: DatabaseReference? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAdresBilgileriBinding.inflate(inflater, container, false)
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        firebaseAuth = FirebaseAuth.getInstance()

        sharedPreferences = requireContext()!!.getSharedPreferences("MY_INFO", Context.MODE_PRIVATE)
        var editor = sharedPreferences.edit()
        profileViewModels = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity()!!.application).create(
            ProfileViewModel::class.java)

        profileViewModels.getUser().observe(viewLifecycleOwner, androidx.lifecycle.Observer { userModel->

            editor.putString("name",userModel.name)
            editor.putString("image",userModel.image)
            editor.putString("uid",userModel.uid)
            editor.putString("status",userModel.status)
            editor.putBoolean("kayitliMi",true)
            editor.apply()


            //Picasso.get().load(userModel.image).into(binding.imgProfile)
        })

        val imm = requireActivity().getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        binding.btnDataDone.setOnClickListener {
            var sehir = binding.editTextSehir.text
            var ilce = binding.editTextIlce.text
            var mahalle = binding.editTextMahalle.text

            var reference = FirebaseDatabase.getInstance().getReference("Adresler").child(firebaseAuth.currentUser!!.uid)

            if (sehir.isNotEmpty() && ilce.isNotEmpty() && mahalle.isNotEmpty()) {
                val map = mapOf(
                    "sehir" to sehir.toString(),
                    "ilce" to ilce.toString(),
                    "mahalle" to mahalle.toString(),
                    "uid" to firebaseAuth!!.uid!!.toString(),
                )
                AdresModel(sehir.toString(),ilce.toString(),mahalle.toString(),firebaseAuth.uid.toString())
                reference.setValue(map)
            }

            val map = mapOf(
                "kayitliMi" to true,
            )
            databaseReference!!.child(firebaseAuth!!.uid!!).updateChildren(map)

            val intent = Intent(this.context, DashBoard::class.java)
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)

            intent.putExtra("yeniKullanici","true")
            startActivity(intent)
        }


        return binding.root
    }
}