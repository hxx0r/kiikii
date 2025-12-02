package com.quark.kiikii.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.quark.kiikii.R
import com.quark.kiikii.databinding.FragmentCheckBinding
import com.quark.kiikii.ui.adapters.CheckTypeAdapter
import com.quark.kiikii.ui.fragments.EmailCheckFragment
import com.quark.kiikii.ui.fragments.PasswordCheckFragment
import com.quark.kiikii.ui.fragments.PhoneCheckFragment

class CheckFragment : Fragment() {

    // ViewBinding для доступа к элементам UI
    private var _binding: FragmentCheckBinding? = null
    private val binding get() = _binding!!

    // Адаптер для ViewPager2
    private lateinit var checkTypeAdapter: CheckTypeAdapter

    // Список табов
    private val tabTitles = listOf(
        "Пароль",
        "Email",
        "Телефон"
    )

    // Список иконок для табов
    private val tabIcons = listOf(
        R.drawable.ic_password,
        R.drawable.ic_email,
        R.drawable.ic_phone
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Инициализируем ViewBinding
        _binding = FragmentCheckBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализируем адаптер для ViewPager2
        setupViewPager()

        // Настраиваем связь между TabLayout и ViewPager2
        setupTabLayout()

        // Настраиваем поведение ViewPager2
        setupViewPagerBehavior()
    }

    /**
     * Настройка ViewPager2 с адаптером
     */
    private fun setupViewPager() {
        // Создаем список фрагментов для каждого типа проверки
        val fragments = listOf<Fragment>(
            PasswordCheckFragment.newInstance(),
            EmailCheckFragment.newInstance(),
            PhoneCheckFragment.newInstance()
        )

        // Инициализируем адаптер
        checkTypeAdapter = CheckTypeAdapter(
            fragments,
            childFragmentManager,
            lifecycle
        )

        // Устанавливаем адаптер для ViewPager2
        binding.viewPagerCheck.adapter = checkTypeAdapter
    }

    /**
     * Связываем TabLayout с ViewPager2
     */
    private fun setupTabLayout() {
        TabLayoutMediator(
            binding.tabLayoutCheck,
            binding.viewPagerCheck
        ) { tab, position ->
            tab.text = tabTitles[position]

            // Устанавливаем иконки для табов
            if (position < tabIcons.size) {
                tab.setIcon(tabIcons[position])
            }
        }.attach()
    }

    /**
     * Настройка поведения ViewPager2
     */
    private fun setupViewPagerBehavior() {
        // Отключаем swipe для ViewPager (опционально)
        // binding.viewPagerCheck.isUserInputEnabled = false

        // Настраиваем ориентацию (горизонтальная по умолчанию)
        binding.viewPagerCheck.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        // Ограничиваем количество страниц, которые сохраняются по бокам
        binding.viewPagerCheck.offscreenPageLimit = 3
    }

    /**
     * Метод для переключения на определенную вкладку
     */
    fun switchToTab(tabIndex: Int) {
        if (tabIndex in 0..2) {
            binding.viewPagerCheck.currentItem = tabIndex
        }
    }

    /**
     * Получить текущий активный фрагмент
     */
    fun getCurrentFragment(): Fragment? {
        return checkTypeAdapter.getFragment(binding.viewPagerCheck.currentItem)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Очищаем ViewBinding при уничтожении View
        _binding = null
    }

    companion object {
        /**
         * Фабричный метод для создания фрагмента
         */
        @JvmStatic
        fun newInstance() = CheckFragment()
    }
}