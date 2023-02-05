package com.example.socialmap.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.socialmap.fragments.Profile.BlogFragment
import com.example.socialmap.fragments.Profile.HakkindaFragment
import com.example.socialmap.fragments.UserInfoViewPager.UserInfoBeceri
import com.example.socialmap.fragments.UserInfoViewPager.UserInfoBlog
import com.example.socialmap.fragments.UserInfoViewPager.UserInfoHakkinda

class ViewPagerAdapterUserInfo(fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                UserInfoHakkinda()
            }
            1 -> {
                UserInfoBeceri()
            }
            2 -> {
                UserInfoBlog()
            }
            else -> {
                Fragment()
            }

        }
    }
}