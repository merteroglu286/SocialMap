package com.example.socialmap.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.socialmap.BecerilerModel
import com.example.socialmap.Constants.AppConstants
import com.example.socialmap.LocalDatabase.Preference
import com.example.socialmap.R
import com.example.socialmap.UserModel
import com.example.socialmap.ViewModels.ProfileViewModel
import com.example.socialmap.activities.DashBoard
import com.example.socialmap.databinding.FragmentGetUserDataBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView


class GetUserData : Fragment() {

    private var _binding: FragmentGetUserDataBinding? = null
    private val binding get() = _binding!!

    private lateinit var profileViewModels: ProfileViewModel

    private var image: Uri? = null
    private lateinit var username: String
    private lateinit var status: String
    private lateinit var imageUrl: String
    private lateinit var uid: String
    private lateinit var firstName: String
    private lateinit var lastName: String
    private var gender: String = "male"

    private var databaseReference: DatabaseReference? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var storageReference: StorageReference? = null

    private lateinit var sharedPreferences: SharedPreferences



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentGetUserDataBinding.inflate(inflater, container, false)
        val view = binding.root

        firebaseAuth = FirebaseAuth.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        sharedPreferences = requireContext()!!.getSharedPreferences("MY_INFO", Context.MODE_PRIVATE)
        var editor = sharedPreferences.edit()
        editor.putBoolean("kayitliMi",false)
        editor.apply()

        binding.imgPickImage.setOnClickListener {
            if (checkStoragePermission())
                pickImage()
            else storageRequestPermission()
        }

        binding.genderRadioGroup.setOnCheckedChangeListener{ group , checkedId ->
            if (checkedId == R.id.male){
                gender = "male"
            }
            if (checkedId == binding.female.id){
                gender = "female"
            }

        }

        binding.btnDataDone.setOnClickListener {

            if (checkData()) {
                beceriKaydet()
                uploadData(username, status, image!!,firstName,lastName,gender)
            }
        }



        return view

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkData(): Boolean {
        username = binding.edtUserName.text.toString().trim()
        status = binding.edtUserStatus.text.toString().trim()
        firstName = binding.firstName.text.toString().trim()
        lastName = binding.lastName.text.toString().trim()

        if  (firstName.isEmpty()){
            binding.firstName.error = "Boş bıraklamaz"
            return false
        }
        else if(lastName.isEmpty()){
            binding.lastName.error = "Boş bıraklamaz"
            return false
        }
        else if(username.isEmpty()) {
            binding.edtUserName.error = "Boş bıraklamaz"
            return false
        }
        else if  (status.isEmpty()) {
            binding.edtUserStatus.error = "Boş bıraklamaz"
            return false
        }

        else if (image == null) {
            Toast.makeText(context, "Image required", Toast.LENGTH_SHORT).show()
            return false

        }
        else return true
    }


    private fun uploadData(name: String, status: String, image: Uri,firstName:String,lastName:String,gender:String) = kotlin.run {

        loading(false)
        storageReference!!.child(firebaseAuth!!.uid + AppConstants.PATH).putFile(image)
            .addOnSuccessListener {
                val task = it.storage.downloadUrl
                task.addOnCompleteListener { uri ->
                    imageUrl = uri.result.toString()
                    uid = firebaseAuth!!.uid!!.toString()
                    val map = mapOf(
                        "name" to name,
                        "firstName" to firstName,
                        "lastName" to lastName,
                        "status" to status,
                        "image" to imageUrl,
                        "uid" to uid,
                        "email" to firebaseAuth!!.currentUser!!.email,
                        "kayitliMi" to false,
                        "gender" to gender,
                        "isletmeAdi" to "",
                        "isletmeTuru" to "",
                        "hesapTipi" to 1
                    )
                    databaseReference!!.child(firebaseAuth!!.uid!!).updateChildren(map)


                    requireActivity().supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.main_container, AdresBilgileriFragment())
                        .commit()

                }
            }
        loading(true)
    }
    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun storageRequestPermission() = ActivityCompat.requestPermissions(
        requireActivity(),
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ), 1000
    )

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1000 ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    pickImage()
                else Toast.makeText(context, "Storage permission denied", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {

            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    image = result.uri
                    binding.imgUser.setImageURI(image)
                }
            }
        }
    }

    private fun pickImage() {
        context?.let {
            CropImage.activity()
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(it, this)
        }
    }

    private fun beceriKaydet(){
        var reference = FirebaseDatabase.getInstance().getReference("Beceriler").child(firebaseAuth!!.uid.toString())

        val map = mapOf(
            "satrancValue" to "0",
            "basketbolValue" to "0",
            "futbolValue" to "0",
            "bilardoValue" to "0",
            "bowlingValue" to "0",
            "tenisValue" to "0",
            "masaTenisiValue" to "0",
            "okeyValue" to "0",
            "paintballValue" to "0",
            "gokartValue" to "0",
            "fifaValue" to "0",
            "pesValue" to "0",
            "cs16Value" to "0",
            "csgoValue" to "0",
            "valorantValue" to "0",
            "lolValue" to "0",
            "uid" to firebaseAuth!!.uid!!.toString()
        )
        BecerilerModel("0","0","0","0",firebaseAuth!!.uid.toString())
        reference.updateChildren(map)
    }

    private fun loading(isLoading:Boolean){

        if(isLoading){
            binding.btnDataDone.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        }else{
            binding.btnDataDone.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }

    }
}