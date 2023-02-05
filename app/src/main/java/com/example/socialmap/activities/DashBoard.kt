package com.example.socialmap.activities

import android.app.AlertDialog
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.example.socialmap.Adapter.ViewPagerAdapterDashboard
import com.example.socialmap.R
import com.example.socialmap.ViewModels.ProfileViewModel
import com.example.socialmap.databinding.ActivityDashBoardBinding
import com.example.socialmap.databinding.DashboardDialogBinding
import com.example.socialmap.fragments.ChatFragment
import com.example.socialmap.fragments.ContactFragment
import com.example.socialmap.fragments.MapsFragment
import com.example.socialmap.fragments.Profile.Profile
import com.example.socialmap.fragments.ProfileIsletme.ProfileIsletmeFragment
import kotlinx.android.synthetic.main.activity_admin_paneli.*

//import org.neidhardt.viewpagerdialog.ViewPagerDialog

class DashBoard : AppCompatActivity() {

    private val rotateOpen : Animation by lazy { AnimationUtils.loadAnimation(this,R.anim.rotate_open_animation) }
    private val rotateClose : Animation by lazy { AnimationUtils.loadAnimation(this,R.anim.rotate_close_animation) }
    private val fromBottom : Animation by lazy { AnimationUtils.loadAnimation(this,R.anim.from_bottom_animation) }
    private val toBottom : Animation by lazy { AnimationUtils.loadAnimation(this,R.anim.to_bottom_animation) }

    private var clicked = false

    private lateinit var navController: NavController
    private lateinit var binding: ActivityDashBoardBinding
    private lateinit var profileViewModels: ProfileViewModel

    private var fragment : Fragment? = null

    private lateinit var dashboardDialogBinding: DashboardDialogBinding
    private lateinit var dialog:  AlertDialog

    private var yeniKullanici : Boolean = false
    private var hesapTipi : Int = 1



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
/*
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.dashboardContainer, ChatFragment()).commit()
            binding.bottomChip.setItemSelected(R.id.btnChat)
        }
        binding.bottomChip.setOnItemSelectedListener { id ->
            when (id) {
                R.id.btnChat -> {
                    fragment = ChatFragment()


                }

                R.id.btnProfile -> {
                    fragment = Profile();

                }

                R.id.btnContact -> fragment = ContactFragment()

                R.id.btnMap -> fragment = MapsFragment()

            }

            fragment!!.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.dashboardContainer, fragment!!)
                    .commit()

            }
        }

 */
/*
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.dashboardContainer) as NavHostFragment
        navController = navHostFragment.navController
        setupWithNavController(binding.bottomNavigationView,navController)

 */

        hesapTipi = intent.getIntExtra("hesapTipi",3)

        Toast.makeText(this,"Hesaptipi $hesapTipi",Toast.LENGTH_SHORT).show()
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.dashboardContainer, MapsFragment()).commit()
            binding.bottomNavigationView.selectedItemId = R.id.btnMap
            binding.bottomBarFab.isVisible = true
            binding.bottomBarFab.isClickable = false
        }

        binding.bottomBarFab.alpha = 0f
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.btnChat -> {
                    replaceFragment(ChatFragment())
                    binding.bottomBarFab.isVisible = false

                }
                R.id.btnMap -> {
                    replaceFragment(MapsFragment())
                    binding.bottomBarFab.isVisible = false
                }
                R.id.btnContact -> {
                    replaceFragment(ContactFragment())
                    binding.bottomBarFab.isVisible = false
                }
                R.id.btnProfile -> {
                    if (hesapTipi == 1){
                        replaceFragment(Profile())
                    }
                    if(hesapTipi == 0){
                        replaceFragment(ProfileIsletmeFragment())
                    }

                    binding.bottomBarFab.isVisible = false

                }

                else ->{

                }
            }
            true
        }

        yeniKullanici = intent.getStringExtra("yeniKullanici").toBoolean()


        if (yeniKullanici){
            val images = listOf(
                R.drawable.intro1,
                R.drawable.intro2,
                R.drawable.intro3,
            )
            val alertDialog = AlertDialog.Builder(this)
            dashboardDialogBinding = DashboardDialogBinding.inflate(layoutInflater)
            alertDialog.setView(dashboardDialogBinding.root)
            val viewPager = dashboardDialogBinding.viewPagerDashboard

            val adapter = ViewPagerAdapterDashboard(images)
            viewPager.adapter = adapter

            val indicator = dashboardDialogBinding.dashboardIndicator
            indicator.setViewPager(viewPager)
            dialog = alertDialog.create()
            dialog.window?.setDimAmount(0.0f);
            dialog.show()
        }


    }


    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.dashboardContainer,fragment)
        fragmentTransaction.commit()
    }

}