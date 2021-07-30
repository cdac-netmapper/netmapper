package com.phamsonhoang.netmapper.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.phamsonhoang.netmapper.fragments.ExamplesFragment
import com.phamsonhoang.netmapper.fragments.HomeFragment

class TabPageAdapter(activity : FragmentActivity, private val tabCount : Int): FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = tabCount

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> HomeFragment()
            1 -> ExamplesFragment()
            else -> HomeFragment()
        }
    }
}