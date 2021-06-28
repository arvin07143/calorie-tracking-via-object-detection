package com.example.fyp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.fyp.databinding.FragmentUserProfileBinding
import com.google.android.material.tabs.TabLayoutMediator

class UserProfile : Fragment() {

lateinit var binding : FragmentUserProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserProfileBinding.inflate(layoutInflater)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tabLayout = binding.profileTabLayout
        val viewPager2 = binding.profilePager
        viewPager2.adapter = PagerAdapter(this)
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            when(position){
                0 -> tab.text = "My Info"
                else -> tab.text = "My Items"
            }
        }.attach()

    }

    private inner class PagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        private val mFragments = arrayOf(UserProfileInfoFragment(), ProfileCustomItemFragment())

        override fun getItemCount(): Int = mFragments.size

        override fun createFragment(position: Int): Fragment {
            Log.e("ATTACH", "ATTACH $position")
            return mFragments[position]
        }

    }

}