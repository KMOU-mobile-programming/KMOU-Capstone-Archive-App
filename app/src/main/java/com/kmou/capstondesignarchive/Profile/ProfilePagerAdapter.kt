package com.kmou.capstondesignarchive.Profile

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ProfilePagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ProfileWorkFragment()
            1 -> ProfileInfoFragment()
            else -> ProfileSaveFragment()
        }
    }
}
