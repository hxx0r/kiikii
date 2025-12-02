package com.quark.kiikii.data

import java.util.Date

data class CheckHistory(
    val id: Long,
    val type: String, // "Пароль", "Email", "Телефон"
    val data: String, // Замаскированные или хэшированные данные
    val isLeaked: Boolean,
    val timestamp: Long,
    val breachCount: Int = 0,
    val breachDetails: String? = null
) {
    fun getFormattedTime(): String {
        // TODO: Форматировать время
        return Date(timestamp).toString()
    }
}