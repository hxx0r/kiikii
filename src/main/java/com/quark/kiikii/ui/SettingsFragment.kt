package com.quark.kiikii.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.quark.kiikii.R
import com.quark.kiikii.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        loadSettings()
    }

    private fun setupListeners() {
        // Переключатель автоочистки
        binding.switchAutoClear.setOnCheckedChangeListener { _, isChecked ->
            // TODO: Сохранить настройку
        }

        // Переключатель уведомлений
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            // TODO: Сохранить настройку
        }

        // Кнопка сброса настроек
        binding.buttonResetSettings.setOnClickListener {
            resetToDefaultSettings()
        }

        // Кнопка о приложении
        binding.buttonAbout.setOnClickListener {
            showAboutDialog()
        }

        // Кнопка конфиденциальности
        binding.buttonPrivacy.setOnClickListener {
            showPrivacyDialog()
        }
    }

    private fun loadSettings() {
        // TODO: Загрузить сохраненные настройки
        binding.switchAutoClear.isChecked = true
        binding.switchNotifications.isChecked = true
        binding.switchDarkTheme.isChecked = false
    }

    private fun resetToDefaultSettings() {
        binding.switchAutoClear.isChecked = true
        binding.switchNotifications.isChecked = true
        binding.switchDarkTheme.isChecked = false
        // TODO: Сохранить настройки по умолчанию
    }

    private fun showAboutDialog() {
        // TODO: Реализовать диалог "О приложении"
    }

    private fun showPrivacyDialog() {
        // TODO: Реализовать диалог "Политика конфиденциальности"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = SettingsFragment()
    }
}