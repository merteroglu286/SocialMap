package com.example.socialmap.fragments.ProfileIsletme

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.socialmap.Adapter.ProfileIsletmePhotosAdapter
import com.example.socialmap.Adapter.ViewPagerAdapter
import com.example.socialmap.Adapter.ViewPagerAdapterDashboard
import com.example.socialmap.Adapter.ViewPagerAdapterIsletme
import com.example.socialmap.R
import com.example.socialmap.Util.AppUtil
import com.example.socialmap.ViewModels.ProfileViewModel
import com.example.socialmap.activities.MainActivity
import com.example.socialmap.databinding.FragmentProfileIsletmeBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_profile_isletme.*


class ProfileIsletmeFragment : Fragment() {

    private lateinit var _binding: FragmentProfileIsletmeBinding
    private val binding get() = _binding!!
    private lateinit var profileViewModels: ProfileViewModel
    private lateinit var appUtil: AppUtil
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileIsletmeBinding.inflate(inflater, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        appUtil = AppUtil()

        profileViewModels = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity()!!.application).create(
            ProfileViewModel::class.java)


        profileViewModels.getUser().observe(viewLifecycleOwner, Observer {  userModel ->
            binding.userModel = userModel
            binding.isletmeName.text = userModel.isletmeAdi
        })

        val images = listOf(
            R.drawable.image,
            R.drawable.zxc,
            R.drawable.image,
            R.drawable.image2,
        )

        val viewPager = binding.viewPagerDashboard

        val adapter = ProfileIsletmePhotosAdapter(images)
        viewPager.adapter = adapter


        val tabLayout= binding.tabLayout
        val viewPager2 = binding.viewPager2

        val adapter2= ViewPagerAdapterIsletme(parentFragmentManager,lifecycle)

        viewPager2.adapter=adapter2

        TabLayoutMediator(tabLayout,viewPager2){tab,position->
            when(position){
                0->{
                    tab.text="Hizmet"
                }
                1->{
                    tab.text="Hakkında"
                }
            }
        }.attach()

        val paint = binding.isletmeName.paint
        val width = paint.measureText(binding.isletmeName.text.toString())
        val textShader: Shader = LinearGradient(0f, 0f, width, binding.isletmeName.textSize, intArrayOf(
            Color.parseColor("#00a1da"),
            Color.parseColor("#00d0fc"),
            /*Color.parseColor("#64B678"),
            Color.parseColor("#478AEA"),*/
            Color.parseColor("#14a0f4")
        ), null, Shader.TileMode.REPEAT)

        binding.isletmeName.paint.setShader(textShader)

        val popupMenu = PopupMenu(requireContext(),binding.btnPopup, Gravity.TOP)

        popupMenu.menuInflater.inflate(R.menu.isletme_menu,popupMenu.menu)

        popupMenu.setOnMenuItemClickListener {menuItem->
            val id = menuItem.itemId

            if (id == R.id.item_1){
                Toast.makeText(requireContext(),"item1", Toast.LENGTH_SHORT).show()
            }
            if (id == R.id.item_1){
                Toast.makeText(requireContext(),"item2", Toast.LENGTH_SHORT).show()
            }
            if (id == R.id.item_4){
                val databaseReference =
                    FirebaseDatabase.getInstance().getReference("Users")
                        .child(firebaseAuth.currentUser!!.uid)

                val map: MutableMap<String, Any> = HashMap()
                map["token"] = ""
                databaseReference.updateChildren(map)
                firebaseAuth.signOut()
                startActivity(Intent(context, MainActivity::class.java))
                requireActivity().finish()
            }

            false
        }

        binding.btnPopup.setOnClickListener {
            popupMenu.show()
        }



        return binding.root

    }

}