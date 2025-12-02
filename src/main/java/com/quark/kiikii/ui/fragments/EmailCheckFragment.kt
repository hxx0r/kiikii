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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLEncoder

class EmailCheckFragment : Fragment() {

    private lateinit var checkEmailButton: Button
    private lateinit var emailInput: EditText
    private lateinit var emailProgressBar: ProgressBar
    private lateinit var emailResultText: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_email_check, container, false)

        checkEmailButton = view.findViewById(R.id.checkEmailButton)
        emailInput = view.findViewById(R.id.emailInput)
        emailProgressBar = view.findViewById(R.id.emailProgressBar)
        emailResultText = view.findViewById(R.id.emailResultText)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
    }

    private fun setupListeners() {
        checkEmailButton.setOnClickListener {
            val email = emailInput.text?.toString()?.trim() ?: ""

            if (email.isEmpty()) {
                showError("Введите email для проверки")
                return@setOnClickListener
            }

            if (!isValidEmail(email)) {
                showError("Введите корректный email адрес")
                return@setOnClickListener
            }

            checkEmail(email)
        }
    }

    private fun checkEmail(email: String) {
        emailProgressBar.visibility = View.VISIBLE
        checkEmailButton.isEnabled = false
        emailResultText.visibility = View.GONE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Для проверки email через HIBP API нужен API ключ
                // Но можно использовать публичный API без ключа в ограниченном режиме
                val encodedEmail = URLEncoder.encode(email, "UTF-8")
                val url = "https://haveibeenpwned.com/api/v3/breachedaccount/$encodedEmail"

                val connection = java.net.URL(url).openConnection() as java.net.HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("User-Agent", "DataBreachChecker-Android-App")
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                val responseCode = connection.responseCode
                val response = if (responseCode == 200) {
                    // Email найден в утечках
                    connection.inputStream.bufferedReader().use { it.readText() }
                } else if (responseCode == 404) {
                    // Email не найден
                    "[]"
                } else {
                    // Ошибка
                    throw Exception("API вернул код $responseCode")
                }

                connection.disconnect()

                withContext(Dispatchers.Main) {
                    emailProgressBar.visibility = View.GONE
                    checkEmailButton.isEnabled = true
                    emailResultText.visibility = View.VISIBLE

                    if (response != "[]" && response.isNotEmpty()) {
                        // Парсим JSON ответ
                        val breaches = parseBreaches(response)
                        val resultText = """
                            ⚠️ Email найден в утечках!
                            
                            Адрес $email найден в ${breaches.size} утечках данных.
                            
                            📧 Утечки:
                            ${breaches.joinToString("\n") { "• ${it.name} (${it.date})" }}
                            
                            🔐 Рекомендации:
                            • Смените пароли на этих сервисах
                            • Включите двухфакторную аутентификацию
                            • Используйте менеджер паролей
                        """.trimIndent()

                        emailResultText.text = resultText
                        emailResultText.setTextColor(resources.getColor(R.color.danger, null))
                    } else {
                        val resultText = """
                            ✅ Email безопасен!
                            
                            Адрес $email не найден в известных утечках данных.
                            
                            🔒 Советы по безопасности:
                            • Используйте уникальные пароли для каждого сервиса
                            • Регулярно обновляйте пароли
                            • Будьте осторожны с фишинг-письмами
                        """.trimIndent()

                        emailResultText.text = resultText
                        emailResultText.setTextColor(resources.getColor(R.color.success, null))
                    }

                    emailInput.text?.clear()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    emailProgressBar.visibility = View.GONE
                    checkEmailButton.isEnabled = true

                    // Если API недоступен, показываем демо-режим
                    if (e.message?.contains("403") == true || e.message?.contains("429") == true) {
                        // API ограничение - показываем демо
                        showDemoEmailCheck(email)
                    } else {
                        showError("Ошибка проверки: ${e.message ?: "неизвестная ошибка"}")
                    }
                }
            }
        }
    }

    private fun parseBreaches(json: String): List<Breach> {
        return try {
            // Простой парсинг JSON
            val breaches = mutableListOf<Breach>()
            val jsonArray = org.json.JSONArray(json)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                breaches.add(
                    Breach(
                        name = obj.getString("Name"),
                        date = obj.optString("BreachDate", "Неизвестно")
                    )
                )
            }
            breaches
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun showDemoEmailCheck(email: String) {
        emailResultText.visibility = View.VISIBLE

        val isLeaked = email.hashCode() % 4 == 0 // 25% шанс

        if (isLeaked) {
            emailResultText.text = """
                ⚠️ Email найден в утечках! (Демо-режим)
                
                В реальной версии используется Have I Been Pwned API.
                Получите API ключ для полноценной проверки.
                
                🔐 Рекомендации:
                • Всегда используйте уникальные пароли
                • Включите двухфакторную аутентификацию
                • Регулярно проверяйте email на hibp.com
            """.trimIndent()
            emailResultText.setTextColor(resources.getColor(R.color.warning, null))
        } else {
            emailResultText.text = """
                ✅ Email безопасен! (Демо-режим)
                
                В реальной версии используется Have I Been Pwned API.
                Получите API ключ для полноценной проверки.
                
                💡 Получите API ключ на:
                • haveibeenpwned.com/API/Key
                • Бесплатно для некоммерческого использования
            """.trimIndent()
            emailResultText.setTextColor(resources.getColor(R.color.info, null))
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
        return emailRegex.matches(email)
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    data class Breach(val name: String, val date: String)

    companion object {
        @JvmStatic
        fun newInstance() = EmailCheckFragment()
    }
}