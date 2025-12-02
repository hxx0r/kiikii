package com.quark.kiikii

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    private val fragments = mutableListOf<Fragment>()
    private val titles = mutableListOf<String>()

    fun addFragment(fragment: Fragment, title: String) {
        fragments.add(fragment)
        titles.add(title)
    }

    fun getPageTitle(position: Int): String {
        return if (position < titles.size) titles[position] else ""
    }

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        return if (position < fragments.size) {
            fragments[position]
        } else {
            // Возвращаем пустой фрагмент вместо null
            createEmptyFragment()
        }
    }

    private fun createEmptyFragment(): Fragment {
        return object : Fragment() {
            override fun onCreateView(
                inflater: android.view.LayoutInflater,
                container: android.view.ViewGroup?,
                savedInstanceState: android.os.Bundle?
            ): android.view.View {
                return android.widget.TextView(requireContext()).apply {
                    text = "Фрагмент не найден"
                    gravity = android.view.Gravity.CENTER
                }
            }
        }
    }
}