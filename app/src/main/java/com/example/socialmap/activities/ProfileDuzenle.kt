package com.example.socialmap.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.socialmap.Constants.AppConstants
import com.example.socialmap.Permission.AppPermission
import com.example.socialmap.ProfileIlgiAlanlariModel
import com.example.socialmap.ProfileIsModel
import com.example.socialmap.ProfileStatusActivity
import com.example.socialmap.R
import com.example.socialmap.ViewModels.ProfileViewModel
import com.example.socialmap.databinding.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class ProfileDuzenle : AppCompatActivity() {

    private lateinit var binding: ActivityProfileDuzenleBinding
    private lateinit var profileViewModels: ProfileViewModel

    private lateinit var dialogLayoutIsDuzenleBinding : DialogLayoutIsDuzenleBinding
    private lateinit var dialogLayoutIlgiAlanlariDuzenleBinding: DialogLayoutIlgiAlanlariDuzenleBinding
    private lateinit var dialog: AlertDialog
    private lateinit var dialog2: AlertDialog

    private lateinit var appPermission: AppPermission
    private lateinit var storageReference: StorageReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileDuzenleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appPermission = AppPermission()
        firebaseAuth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("userData", Context.MODE_PRIVATE)

        profileViewModels = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(ProfileViewModel::class.java)

        profileViewModels.getUser().observe(this, androidx.lifecycle.Observer { userModel->
            binding.userModel = userModel

            //Picasso.get().load(userModel.image).into(binding.imgProfile)
        })

        binding.editUserNameProfileDuzenle.setOnClickListener {
            val intent = Intent(it.context, ProfileNameActivity::class.java)
            it.context.startActivity(intent)
            binding.editUserNameProfileDuzenle.clearFocus()
        }
        binding.editUserNameProfileDuzenle.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus){
                val intent = Intent(v.context, ProfileNameActivity::class.java)
                v.context.startActivity(intent)
                binding.editUserNameProfileDuzenle.clearFocus()
            }
        }

        binding.editUserNameProfileDuzenle.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus){
                val intent = Intent(v.context, ProfileNameActivity::class.java)
                v.context.startActivity(intent)
                binding.editUserNameProfileDuzenle.clearFocus()
            }
        }

        binding.editUserStatusProfileDuzenle.setOnClickListener {
            val intent = Intent(it.context, ProfileStatusActivity::class.java)
            it.context.startActivity(intent)
            binding.editUserStatusProfileDuzenle.clearFocus()
        }
        binding.editUserStatusProfileDuzenle.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus){
                val intent = Intent(v.context, ProfileStatusActivity::class.java)
                v.context.startActivity(intent)
                binding.editUserStatusProfileDuzenle.clearFocus()
            }
        }


        binding.profileDuzenleKaydet.setOnClickListener {
            finish()
        }

        binding.imgEditIs.setOnClickListener{
            getIsDialog()
        }

        binding.imgEditIlgiAlanlari.setOnClickListener {
            getIsDialog2()
        }

        binding.imgPickImage.setOnClickListener{
            if(appPermission.isStorageOk(this)){
                pickImage()
            }
        }
    }

    private fun pickImage() {
        CropImage.activity().setCropShape(CropImageView.CropShape.OVAL)
            .start(this)
    }

    private fun getIsDialog() {

        val alertDialog = AlertDialog.Builder(this)
        dialogLayoutIsDuzenleBinding = DialogLayoutIsDuzenleBinding.inflate(layoutInflater)
        alertDialog.setView(dialogLayoutIsDuzenleBinding.root)

        dialogLayoutIsDuzenleBinding.btnEditIs.setOnClickListener {

            val eklenen = dialogLayoutIsDuzenleBinding.addUserIs.text.toString()

            var reference = FirebaseDatabase.getInstance().getReference("eklenenIsler").child(firebaseAuth.currentUser!!.uid)

            if (eklenen.isNotEmpty()) {
                val map = mapOf(
                    "eklenen" to eklenen,
                    "uid" to firebaseAuth!!.uid!!.toString(),
                )
                ProfileIsModel(eklenen,firebaseAuth.uid.toString())
                reference.push().setValue(map)
                dialog.dismiss()
            }
        }
        dialog = alertDialog.create()
        dialog.show()


    }

    private fun getIsDialog2() {

        val alertDialog = AlertDialog.Builder(this)
        dialogLayoutIlgiAlanlariDuzenleBinding = DialogLayoutIlgiAlanlariDuzenleBinding.inflate(layoutInflater)
        alertDialog.setView(dialogLayoutIlgiAlanlariDuzenleBinding.root)

        dialogLayoutIlgiAlanlariDuzenleBinding.btnEditIlgiAlanlari.setOnClickListener {

            val eklenen = dialogLayoutIlgiAlanlariDuzenleBinding.addUserIlgiAlanlari.text.toString()

            var reference = FirebaseDatabase.getInstance().getReference("ilgiAlanlari").child(firebaseAuth.currentUser!!.uid)

            if (eklenen.isNotEmpty()) {
                val map = mapOf(
                    "eklenen" to eklenen,
                    "uid" to firebaseAuth!!.uid!!.toString(),
                )
                ProfileIlgiAlanlariModel(eklenen,firebaseAuth.uid.toString())
                reference.push().setValue(map)
                dialog2.dismiss()
            }
        }
        dialog2 = alertDialog.create()
        dialog2.show()


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            100 -> {
                if (data != null) {
                    val userName = data.getStringExtra("name")
                    profileViewModels.updateName(userName!!)
                    val editor = sharedPreferences.edit()
                    editor.putString("myName", userName).apply()
                }

            }
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                if (data != null) {

                    val result = CropImage.getActivityResult(data)
                    if (resultCode == Activity.RESULT_OK) {
                        uploadImage(result.uri)
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            AppConstants.STORAGE_PERMISSION -> {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    pickImage()
                else Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadImage(imageUri: Uri) {

        storageReference = FirebaseStorage.getInstance().reference
        storageReference.child(firebaseAuth.uid + AppConstants.PATH).putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                val task = taskSnapshot.storage.downloadUrl
                task.addOnCompleteListener {
                    if (it.isSuccessful) {
                        val imagePath = it.result.toString()

                        val editor = sharedPreferences.edit()
                        editor.putString("myImage", imagePath).apply()
                        profileViewModels.updateImage(imagePath)
                    }
                }
            }
    }
}