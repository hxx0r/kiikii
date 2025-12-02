package com.quark.kiikii.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.quark.kiikii.R
import com.quark.kiikii.network.HIBPApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest

class PasswordCheckFragment : Fragment() {

    private lateinit var checkPasswordButton: Button
    private lateinit var passwordInput: EditText
    private lateinit var passwordProgressBar: ProgressBar
    private lateinit var passwordResultText: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_password_check, container, false)

        checkPasswordButton = view.findViewById(R.id.checkPasswordButton)
        passwordInput = view.findViewById(R.id.passwordInput)
        passwordProgressBar = view.findViewById(R.id.passwordProgressBar)
        passwordResultText = view.findViewById(R.id.passwordResultText)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
    }

    private fun setupListeners() {
        checkPasswordButton.setOnClickListener {
            val password = passwordInput.text?.toString() ?: ""

            if (password.isBlank()) {
                showError("Введите пароль для проверки")
                return@setOnClickListener
            }

            if (password.length < 4) {
                showError("Пароль должен содержать минимум 4 символа")
                return@setOnClickListener
            }

            checkPassword(password)
        }
    }

    private fun checkPassword(password: String) {
        passwordProgressBar.visibility = View.VISIBLE
        checkPasswordButton.isEnabled = false
        passwordResultText.visibility = View.GONE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Генерируем SHA-1 хэш пароля
                val hash = sha1(password).uppercase()

                // Используем k-анонимность: отправляем только первые 5 символов хэша
                val prefix = hash.substring(0, 5)
                val suffix = hash.substring(5)

                // Выполняем запрос к HIBP API
                val response = HIBPApi.service.getPasswordRange(prefix)

                // Проверяем, есть ли суффикс в ответе
                val isLeaked = checkHashInResponse(response, suffix)
                val breachCount = if (isLeaked) countBreaches(response, suffix) else 0

                withContext(Dispatchers.Main) {
                    passwordProgressBar.visibility = View.GONE
                    checkPasswordButton.isEnabled = true
                    passwordResultText.visibility = View.VISIBLE

                    if (isLeaked) {
                        val resultText = """
                            ⚠️ ПАРоль найден в утечках!
                            
                            Этот пароль был обнаружен в $breachCount утечках данных.
                            
                            🔐 Рекомендации:
                            • Немедленно смените этот пароль
                            • Используйте уникальный пароль для каждого сервиса
                            • Включите двухфакторную аутентификацию
                            
                            📊 Статистика:
                            • Хэш: ${hash.substring(0, 10)}...
                            • Отправлено в API: $prefix...
                            • Реальный пароль НЕ передавался
                        """.trimIndent()

                        passwordResultText.text = resultText
                        passwordResultText.setTextColor(resources.getColor(R.color.danger, null))
                    } else {
                        val resultText = """
                            ✅ Пароль безопасен!
                            
                            Этот пароль не найден в известных утечках данных.
                            
                            💡 Советы по безопасности:
                            • Используйте пароли длиной от 12 символов
                            • Добавляйте цифры, заглавные буквы и специальные символы
                            • Регулярно обновляйте важные пароли
                            
                            🔒 Технические детали:
                            • Использована k-анонимность для конфиденциальности
                            • Проверено через Have I Been Pwned API
                            • Ваш пароль никогда не покидал устройство
                        """.trimIndent()

                        passwordResultText.text = resultText
                        passwordResultText.setTextColor(resources.getColor(R.color.success, null))
                    }

                    // Очищаем поле ввода после проверки
                    passwordInput.text?.clear()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    passwordProgressBar.visibility = View.GONE
                    checkPasswordButton.isEnabled = true
                    showError("Ошибка проверки: ${e.message ?: "неизвестная ошибка"}")
                }
            }
        }
    }

    private fun sha1(input: String): String {
        val digest = MessageDigest.getInstance("SHA-1")
        val result = digest.digest(input.toByteArray(Charsets.UTF_8))
        return result.joinToString("") { "%02x".format(it) }
    }

    private fun checkHashInResponse(response: String, suffix: String): Boolean {
        // Ответ содержит строки вида "SUFFIX:COUNT"
        val lines = response.lines()
        return lines.any { line ->
            val parts = line.split(":")
            parts.first().equals(suffix, ignoreCase = true)
        }
    }

    private fun countBreaches(response: String, suffix: String): Int {
        val lines = response.lines()
        lines.forEach { line ->
            val parts = line.split(":")
            if (parts.first().equals(suffix, ignoreCase = true)) {
                return parts.getOrNull(1)?.toIntOrNull() ?: 1
            }
        }
        return 1
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    companion object {
        @JvmStatic
        fun newInstance() = PasswordCheckFragment()
    }
}