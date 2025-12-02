package com.quark.kiikii

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.quark.kiikii.ui.CheckFragment
import com.quark.kiikii.ui.HistoryFragment
import com.quark.kiikii.ui.SettingsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var adapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            setupViewPager()
        } catch (e: Exception) {
            Log.e("MainActivity", "Ошибка при настройке ViewPager", e)
            Toast.makeText(this, "Ошибка инициализации: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupViewPager() {
        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)

        if (tabLayout == null) {
            throw IllegalStateException("TabLayout не найден в разметке")
        }

        if (viewPager == null) {
            throw IllegalStateException("ViewPager2 не найден в разметке")
        }

        adapter = ViewPagerAdapter(this)

        // Проверяем, что фрагменты создаются корректно
        try {
            val checkFragment = CheckFragment.newInstance()
            val historyFragment = HistoryFragment.newInstance()
            val settingsFragment = SettingsFragment.newInstance()

            adapter.addFragment(checkFragment, "Проверка")
            adapter.addFragment(historyFragment, "История")
            adapter.addFragment(settingsFragment, "Настройки")

            Log.d("MainActivity", "Добавлено фрагментов: ${adapter.itemCount}")

        } catch (e: Exception) {
            Log.e("MainActivity", "Ошибка при создании фрагментов", e)
            Toast.makeText(this, "Ошибка создания фрагментов", Toast.LENGTH_SHORT).show()
            return
        }

        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getPageTitle(position)
        }.attach()

        // Проверяем, что адаптер установлен
        if (viewPager.adapter == null) {
            Log.e("MainActivity", "Адаптер не установлен для ViewPager2")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Очистка ссылок
        if (::adapter.isInitialized) {
            viewPager.adapter = null
        }
    }
}