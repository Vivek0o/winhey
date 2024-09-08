package com.example.winhey.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.winhey.R
import com.example.winhey.data.local.GameHistoryItem

class GameHistoryAdapter(
    private val historyList: List<GameHistoryItem>
) : RecyclerView.Adapter<GameHistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gameId: TextView = itemView.findViewById(R.id.history_game_id)
        val participants: TextView = itemView.findViewById(R.id.history_participants_number)
        val optionSelected: TextView = itemView.findViewById(R.id.history_option_selected)
        val result: TextView = itemView.findViewById(R.id.history_result)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_game_history, parent, false)
        return HistoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val historyItem = historyList[position]

        // Bind data to the views
        holder.gameId.text = historyItem.gameId
        holder.participants.text = historyItem.participants
        holder.optionSelected.text = historyItem.optionSelected
        holder.result.text = historyItem.result
    }

    override fun getItemCount() = historyList.size
}
