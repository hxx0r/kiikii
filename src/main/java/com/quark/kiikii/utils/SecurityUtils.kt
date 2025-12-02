package com.quark.kiikii.utils



import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object SecurityUtils {

    /**
     * SHA-1 хэширование
     */
    fun sha1(input: String): String {
        val digest = MessageDigest.getInstance("SHA-1")
        val result = digest.digest(input.toByteArray(Charsets.UTF_8))
        return bytesToHex(result)
    }

    /**
     * SHA-256 хэширование
     */
    fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val result = digest.digest(input.toByteArray(Charsets.UTF_8))
        return bytesToHex(result)
    }

    /**
     * HMAC-SHA256
     */
    fun hmacSha256(data: String, key: String): String {
        val secretKeySpec = SecretKeySpec(key.toByteArray(Charsets.UTF_8), "HmacSHA256")
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(secretKeySpec)
        val result = mac.doFinal(data.toByteArray(Charsets.UTF_8))
        return bytesToHex(result)
    }

    /**
     * Конвертация байтов в hex строку
     */
    private fun bytesToHex(bytes: ByteArray): String {
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Генерация соли для паролей
     */
    fun generateSalt(): String {
        val secureRandom = java.security.SecureRandom()
        val salt = ByteArray(16)
        secureRandom.nextBytes(salt)
        return bytesToHex(salt)
    }

    /**
     * Нормализация email (приведение к нижнему регистру и удаление пробелов)
     */
    fun normalizeEmail(email: String): String {
        return email.trim().lowercase()
    }

    /**
     * Нормализация телефона (оставляем только цифры)
     */
    fun normalizePhone(phone: String): String {
        return phone.replace(Regex("[^0-9]"), "")
    }
}