package com.quark.kiikii.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quark.kiikii.R

class HistoryAdapter(
    private val items: List<CheckHistory>,
    private val onItemClick: (CheckHistory) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textType: TextView = view.findViewById(R.id.textHistoryType)
        val textStatus: TextView = view.findViewById(R.id.textHistoryStatus)
        val textTime: TextView = view.findViewById(R.id.textHistoryTime)
        val textBreachCount: TextView = view.findViewById(R.id.textBreachCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.textType.text = "Тип: ${item.type}"

        if (item.isLeaked) {
            holder.textStatus.text = "⚠️ Найден в утечках"
            holder.textStatus.setTextColor(holder.itemView.context.getColor(R.color.danger))
        } else {
            holder.textStatus.text = "✅ Безопасен"
            holder.textStatus.setTextColor(holder.itemView.context.getColor(R.color.success))
        }

        holder.textTime.text = item.getFormattedTime()

        if (item.breachCount > 0) {
            holder.textBreachCount.text = "Утечек: ${item.breachCount}"
            holder.textBreachCount.visibility = View.VISIBLE
        } else {
            holder.textBreachCount.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = items.size
}