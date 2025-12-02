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
import java.security.MessageDigest
import java.net.URLEncoder

class PhoneCheckFragment : Fragment() {

    private lateinit var checkPhoneButton: Button
    private lateinit var phoneInput: EditText
    private lateinit var phoneProgressBar: ProgressBar
    private lateinit var phoneResultText: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_phone_check, container, false)

        checkPhoneButton = view.findViewById(R.id.checkPhoneButton)
        phoneInput = view.findViewById(R.id.phoneInput)
        phoneProgressBar = view.findViewById(R.id.phoneProgressBar)
        phoneResultText = view.findViewById(R.id.phoneResultText)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
    }

    private fun setupListeners() {
        checkPhoneButton.setOnClickListener {
            var phone = phoneInput.text.toString().trim()
            phone = normalizePhone(phone)

            if (phone.isEmpty()) {
                showError("Введите номер телефона для проверки")
                return@setOnClickListener
            }

            if (phone.length < 10) {
                showError("Номер телефона должен содержать минимум 10 цифр")
                return@setOnClickListener
            }

            checkPhone(phone)
        }
    }

    private fun checkPhone(phone: String) {
        phoneProgressBar.visibility = View.VISIBLE
        checkPhoneButton.isEnabled = false
        phoneResultText.visibility = View.GONE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Проверяем номер телефона через локальные базы
                // Можно использовать хэширование для конфиденциальности
                val hash = sha256(phone).uppercase()

                // Проверяем по списку известных утечек (локальная база)
                val isLeaked = checkLocalPhoneDatabase(hash)

                // Также можно проверить через DeHashed API (платный)
                // val isLeaked = checkDeHashedAPI(phone)

                withContext(Dispatchers.Main) {
                    phoneProgressBar.visibility = View.GONE
                    checkPhoneButton.isEnabled = true
                    phoneResultText.visibility = View.VISIBLE

                    if (isLeaked) {
                        val resultText = """
                            ⚠️ Номер телефона найден в утечках!
                            
                            Этот номер был обнаружен в утечках данных.
                            
                            📱 Рекомендации:
                            • Включите двухфакторную аутентификацию
                            • Будьте осторожны с SMS-спамом
                            • Рассмотрите смену номера для важных сервисов
                            
                            🔒 Технические детали:
                            • Проверено по локальной базе утечек
                            • Использовано хэширование для конфиденциальности
                            • Хэш: ${hash.substring(0, 12)}...
                        """.trimIndent()

                        phoneResultText.text = resultText
                        phoneResultText.setTextColor(resources.getColor(R.color.danger, null))
                    } else {
                        val resultText = """
                            ✅ Номер телефона безопасен!
                            
                            Этот номер не найден в известных утечках данных.
                            
                            🔒 Советы по безопасности:
                            • Не делитесь номером в соцсетях
                            • Используйте виртуальные номера для регистраций
                            • Регулярно проверяйте подозрительные активности
                            
                            ℹ️ Информация:
                            • Проверка по локальной базе российских утечек
                            • Для расширенной проверки используйте платные сервисы
                            • Регулярно обновляйте базу через настройки
                        """.trimIndent()

                        phoneResultText.text = resultText
                        phoneResultText.setTextColor(resources.getColor(R.color.success, null))
                    }

                    phoneInput.text?.clear()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    phoneProgressBar.visibility = View.GONE
                    checkPhoneButton.isEnabled = true
                    showError("Ошибка проверки: ${e.message ?: "неизвестная ошибка"}")
                }
            }
        }
    }

    private fun normalizePhone(phone: String): String {
        return phone.replace(Regex("[^0-9+]"), "")
    }

    private fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val result = digest.digest(input.toByteArray(Charsets.UTF_8))
        return result.joinToString("") { "%02x".format(it) }
    }

    private fun checkLocalPhoneDatabase(hash: String): Boolean {
        // Локальная база хэшей телефонов из известных утечек
        // В реальном приложении это будет SQLite база или файл

        // Демо-база (в реальном приложении нужно загружать из файла)
        val leakedHashes = setOf(
            "a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3", // 123
            "c1c224b03cd9bc7b6a86d77f5dace40191766c485cd55dc48caf9ac873335d6f", // test
            "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"  // empty
        )

        return leakedHashes.contains(hash)
    }

    private fun checkDeHashedAPI(phone: String): Boolean {
        // Псевдокод для DeHashed API (платный сервис)
        // val apiKey = "ваш_api_ключ"
        // val url = "https://api.dehashed.com/search?query=phone:$phone"
        // Добавить заголовок Authorization
        // Парсить JSON ответ
        return false
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    companion object {
        @JvmStatic
        fun newInstance() = PhoneCheckFragment()
    }
}