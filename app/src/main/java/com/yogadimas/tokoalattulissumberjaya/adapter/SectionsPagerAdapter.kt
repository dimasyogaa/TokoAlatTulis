package com.yogadimas.tokoalattulissumberjaya.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.yogadimas.tokoalattulissumberjaya.fragment.BarangFragment
import com.yogadimas.tokoalattulissumberjaya.fragment.PemasokFragment
import com.yogadimas.tokoalattulissumberjaya.fragment.PemesananFragment

class SectionsPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity){
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = PemesananFragment()
            1 -> fragment = BarangFragment()
            2 -> fragment = PemasokFragment()
        }
        return fragment as Fragment
    }
}