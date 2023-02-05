package com.example.socialmap.fragments.Profile

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.socialmap.Adapter.ViewPagerAdapter
import com.example.socialmap.Constants.AppConstants
import com.example.socialmap.Permission.AppPermission
import com.example.socialmap.R
import com.example.socialmap.Util.AppUtil
import com.example.socialmap.ViewModels.ProfileViewModel
import com.example.socialmap.activities.MainActivity
import com.example.socialmap.activities.ProfileDuzenle
import com.example.socialmap.databinding.DialogLayoutIsDuzenleBinding
import com.example.socialmap.databinding.FragmentProfileBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView


class Profile : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var dialogLayoutBinding : DialogLayoutIsDuzenleBinding
    private lateinit var profileViewModels: ProfileViewModel

    private lateinit var dialog: AlertDialog
    private lateinit var appPermission: AppPermission

    private lateinit var storageReference: StorageReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var databaseReference: DatabaseReference
    private lateinit var appUtil: AppUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater,
            com.example.socialmap.R.layout.fragment_profile,container,false)


        sharedPreferences = requireContext()!!.getSharedPreferences("MY_INFO", Context.MODE_PRIVATE)
        appUtil = AppUtil()

        binding.userName.text = sharedPreferences.getString("name","")
        //Picasso.get().load(sharedPreferences.getString("image","")).into(binding.imgProfile)

        Glide.with(this).load(sharedPreferences.getString("image","")).into(binding.imgProfile)

        val tabLayout= binding.tabLayout
        val viewPager2 = binding.viewPager2

        val adapter= ViewPagerAdapter(parentFragmentManager,lifecycle)

        viewPager2.adapter=adapter

        TabLayoutMediator(tabLayout,viewPager2){tab,position->
            when(position){
                0->{
                    tab.text="Hakkında"
                }
                1->{
                    tab.text="Beceri"
                }
                2->{
                    tab.text="Blog"
                }
            }
        }.attach()

        val paint = binding.userName.paint
        val width = paint.measureText(binding.userName.text.toString())
        val textShader: Shader = LinearGradient(0f, 0f, width, binding.userName.textSize, intArrayOf(
            Color.parseColor("#00a1da"),
            Color.parseColor("#00d0fc"),
            /*Color.parseColor("#64B678"),
            Color.parseColor("#478AEA"),*/
            Color.parseColor("#14a0f4")
        ), null, Shader.TileMode.REPEAT)

        binding.userName.paint.setShader(textShader)
        //binding.btnMessage.paint.setShader(textShader)

        val popupMenu = PopupMenu(requireContext(),binding.btnPopup, Gravity.TOP)

        popupMenu.menuInflater.inflate(R.menu.profile_menu,popupMenu.menu)

        popupMenu.setOnMenuItemClickListener {menuItem->
            val id = menuItem.itemId

            if (id == R.id.item_1){
                Toast.makeText(requireContext(),"item1",Toast.LENGTH_SHORT).show()
            }
            if (id == R.id.item_4){
                val databaseReference =
                    FirebaseDatabase.getInstance().getReference("Users")
                        .child(appUtil.getUID()!!)

                val map: MutableMap<String, Any> = HashMap()
                map["token"] = ""
                databaseReference.updateChildren(map)
                val settings =
                    requireContext().getSharedPreferences("MY_INFO", Context.MODE_PRIVATE)
                settings.edit().clear().commit()
                val settings2 =
                    requireContext().getSharedPreferences("Conversations", Context.MODE_PRIVATE)
                settings.edit().clear().commit()
                firebaseAuth.signOut()
                startActivity(Intent(context,MainActivity::class.java))
                requireActivity().finish()
            }

            false
        }

        binding.btnPopup.setOnClickListener {
            popupMenu.show()
        }


        binding.btnProfileDuzenle.setOnClickListener {
            val intent = Intent(it.context, ProfileDuzenle::class.java)
            it.context.startActivity(intent)
        }


        binding.progressRate.text = (binding.circularProgressImage.progress/10).toString()


        appPermission = AppPermission()
        firebaseAuth = FirebaseAuth.getInstance()
        //sharedPreferences = requireContext()!!.getSharedPreferences("userData", Context.MODE_PRIVATE)






        /*
        profileBinding.imgPickImage.setOnClickListener{
            if(appPermission.isStorageOk(requireContext())){
                pickImage()
            }
        }

        profileBinding.imgEditStatus.setOnClickListener {
            getStatusDialog()
        }

        binding.logOut.setOnClickListener{
            firebaseAuth.signOut()
            startActivity(Intent(context, MainActivity::class.java))
            requireActivity().finish()
        }
*/

        return binding.root
    }


    private fun pickImage() {
        CropImage.activity().setCropShape(CropImageView.CropShape.OVAL)
            .start(requireContext(), this)
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
                else Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
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