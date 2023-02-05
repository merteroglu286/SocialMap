package com.example.socialmap

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.socialmap.Constants.AppConstants
import com.example.socialmap.databinding.ActivityAdminPaneliBinding
import com.example.socialmap.fragments.AdresBilgileriFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class AdminPaneli : AppCompatActivity() {

    private lateinit var binding: ActivityAdminPaneliBinding
    private var image: Uri? = null
    private lateinit var imageUrl: String
    private lateinit var uid: String

    private var databaseReference: DatabaseReference? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var storageReference: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminPaneliBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        databaseReference = FirebaseDatabase.getInstance().getReference("IsletmeFotolari")

        binding.imgPickImage.setOnClickListener {
            if (checkStoragePermission())
                pickImage()
            else storageRequestPermission()
        }
        binding.fotoEkle.setOnClickListener{
            storageReference!!.child(firebaseAuth!!.uid + "/IsletmeImages/${binding.imageId.text}").putFile(image!!)
                .addOnSuccessListener {
                    val task = it.storage.downloadUrl
                    task.addOnCompleteListener { uri ->
                        imageUrl = uri.result.toString()
                        uid = firebaseAuth!!.uid!!.toString()
                        val map = mapOf(
                            "image" to imageUrl,
                        )
                        databaseReference!!.child(firebaseAuth!!.uid!!).push().setValue(map)
                    }
                }
        }

        binding.isletmeEkle.setOnClickListener {

        }
    }


    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun storageRequestPermission() = ActivityCompat.requestPermissions(
        this,
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
                else Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {

            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    image = result.uri
                }
            }
        }
    }

    private fun pickImage() {
        applicationContext?.let {
            CropImage.activity()
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .start(this)
        }
    }
}