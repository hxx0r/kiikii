package com.quark.kiikii


import android.content.Context
import android.content.res.AssetManager
import java.security.MessageDigest

class PhoneDatabase(context: Context) {

    private val leakedHashes = mutableSetOf<String>()

    init {
        loadFromAssets(context.assets)
    }

    private fun loadFromAssets(assets: AssetManager) {
        try {
            // Загружаем файл с хэшами телефонов из assets
            val inputStream = assets.open("phone_hashes.txt")
            val lines = inputStream.bufferedReader().readLines()

            leakedHashes.addAll(lines.map { it.trim() }.filter { it.isNotBlank() })
            inputStream.close()

        } catch (e: Exception) {
            // Если файла нет, используем демо-набор
            loadDemoHashes()
        }
    }

    private fun loadDemoHashes() {
        // Демо-хэши для тестирования
        val demoPhones = listOf(
            "79991234567",
            "+79161234567",
            "89161234567",
            "9111234567"
        )

        demoPhones.forEach { phone ->
            leakedHashes.add(sha256(phone))
        }
    }

    fun isPhoneLeaked(phone: String): Boolean {
        val hash = sha256(phone)
        return leakedHashes.contains(hash)
    }

    private fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val result = digest.digest(input.toByteArray(Charsets.UTF_8))
        return result.joinToString("") { "%02x".format(it) }
    }

    fun getLeakedCount(): Int = leakedHashes.size
}