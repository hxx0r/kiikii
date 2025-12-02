package com.quark.kiikii.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class CheckTypeAdapter(
    private val fragments: List<Fragment>,
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]

    /**
     * Получить фрагмент по позиции
     * В ViewPager2 это сложнее, но можно так:
     */
    fun getFragment(position: Int): Fragment? {
        // В ViewPager2 фрагменты создаются автоматически,
        // но мы можем вернуть их из нашего списка
        return fragments.getOrNull(position)
    }
}