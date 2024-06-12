package com.example.winhey.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.winhey.R
import com.example.winhey.data.models.Player
import com.google.android.material.card.MaterialCardView

class UserDetailAdapter(
    private val itemList: List<Player>?,
    private var cardClickClickListener: CardClickClickListener
) : RecyclerView.Adapter<UserDetailAdapter.UserDetailViewHolder>() {

    class UserDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.userName)
        val totalWon: TextView = itemView.findViewById(R.id.totalWon)
        val cardView: MaterialCardView = itemView.findViewById(R.id.userDetailsCardView)
    }

    interface CardClickClickListener {
        fun onCardClicked(player: Player?, view: View)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserDetailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_detail_item_card, parent, false)
        return UserDetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserDetailViewHolder, position: Int) {
        val item = itemList?.get(position)
        holder.name.text = item?.name
        holder.totalWon.text = item?.totalWon.toString()
        holder.cardView.setOnClickListener {view ->
            cardClickClickListener.onCardClicked(itemList?.get(position), view)
        }
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }
}
