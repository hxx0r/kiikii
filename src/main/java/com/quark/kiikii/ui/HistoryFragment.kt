package com.quark.kiikii.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.quark.kiikii.R
import com.quark.kiikii.data.CheckHistory
import com.quark.kiikii.data.HistoryAdapter
import com.quark.kiikii.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    // Временные данные для демонстрации
    private val demoHistory = listOf(
        CheckHistory(
            id = 1,
            type = "Пароль",
            data = "*******", // Маскируем данные
            isLeaked = true,
            timestamp = System.currentTimeMillis() - 86400000, // 1 день назад
            breachCount = 3
        ),
        CheckHistory(
            id = 2,
            type = "Email",
            data = "test@example.com",
            isLeaked = false,
            timestamp = System.currentTimeMillis() - 172800000, // 2 дня назад
            breachCount = 0
        ),
        CheckHistory(
            id = 3,
            type = "Телефон",
            data = "+7 (XXX) XXX-XX-XX",
            isLeaked = true,
            timestamp = System.currentTimeMillis() - 259200000, // 3 дня назад
            breachCount = 1
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
    }

    private fun setupRecyclerView() {
        val adapter = HistoryAdapter(demoHistory) { historyItem ->
            // Обработка клика по элементу истории
            showHistoryDetails(historyItem)
        }

        binding.recyclerViewHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
        }

        // Проверяем, есть ли история
        if (demoHistory.isEmpty()) {
            binding.textEmptyHistory.visibility = View.VISIBLE
            binding.recyclerViewHistory.visibility = View.GONE
        } else {
            binding.textEmptyHistory.visibility = View.GONE
            binding.recyclerViewHistory.visibility = View.VISIBLE
        }
    }

    private fun setupListeners() {
        binding.buttonClearHistory.setOnClickListener {
            // TODO: Реализовать очистку истории
            binding.textEmptyHistory.visibility = View.VISIBLE
            binding.recyclerViewHistory.visibility = View.GONE
        }

        binding.buttonExportHistory.setOnClickListener {
            // TODO: Реализовать экспорт истории
        }
    }

    private fun showHistoryDetails(historyItem: CheckHistory) {
        // TODO: Реализовать показ деталей проверки
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = HistoryFragment()
    }
}