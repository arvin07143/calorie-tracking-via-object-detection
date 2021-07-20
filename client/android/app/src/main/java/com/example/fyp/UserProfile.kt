package com.example.fyp

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.fyp.data.repository.MealRepository
import com.example.fyp.databinding.FragmentUserProfileBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserProfile : Fragment() {
    @Inject
    lateinit var repository: MealRepository
    lateinit var binding: FragmentUserProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_nav, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentUserProfileBinding.inflate(layoutInflater)

        if (FirebaseAuth.getInstance().currentUser?.photoUrl == null) {
            binding.profilePicture.setImageResource(R.drawable.ic_baseline_account_circle_24)
        } else {
            Picasso.get().load(FirebaseAuth.getInstance().currentUser?.photoUrl)
                .into(binding.profilePicture)
        }

        binding.tvName.text = FirebaseAuth.getInstance().currentUser?.displayName

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