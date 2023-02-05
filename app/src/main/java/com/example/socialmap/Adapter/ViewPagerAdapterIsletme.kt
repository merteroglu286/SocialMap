package com.example.socialmap.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.socialmap.fragments.Profile.BeceriFragment
import com.example.socialmap.fragments.Profile.BlogFragment
import com.example.socialmap.fragments.Profile.HakkindaFragment
import com.example.socialmap.fragments.ProfileIsletme.IsletmeHakkindaFragment
import com.example.socialmap.fragments.ProfileIsletme.IsletmeHizmetFragment

class ViewPagerAdapterIsletme(fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                IsletmeHizmetFragment()
            }
            1 -> {
                IsletmeHakkindaFragment()
            }
            else -> {
                Fragment()
            }

        }
    }
}