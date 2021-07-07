package com.example.fyp

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.fyp.data.repository.MealRepository
import com.example.fyp.databinding.FragmentUserProfileBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.Period
import javax.inject.Inject

@AndroidEntryPoint
class UserProfile : Fragment() {
    @Inject
    lateinit var repository: MealRepository
    lateinit var binding: FragmentUserProfileBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentUserProfileBinding.inflate(layoutInflater)

        Picasso.get().load(FirebaseAuth.getInstance().currentUser?.photoUrl).into(binding.imageView)
        repository.getUserProfileInfo().let {
            binding.tvHeight.text = it.height.toString()
            binding.tvWeight.text = it.weight.toString()
            binding.tvAge.text = Period.between(
                LocalDate.of(it.dob.year + 1900, it.dob.month + 1, it.dob.date), LocalDate.now()
            ).years.toString()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tabLayout = binding.profileTabLayout
        val viewPager2 = binding.profilePager
        viewPager2.adapter = PagerAdapter(this)
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            when (position) {
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