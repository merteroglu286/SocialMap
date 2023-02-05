package com.example.socialmap.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.socialmap.fragments.Profile.BeceriFragment
import com.example.socialmap.fragments.Profile.BlogFragment
import com.example.socialmap.fragments.Profile.HakkindaFragment

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                HakkindaFragment()
            }
            1 -> {
                BeceriFragment()
            }
            2 -> {
                BlogFragment()
            }
            else -> {
                Fragment()
            }

        }
    }
}