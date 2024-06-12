package com.example.winhey.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.winhey.R
import com.example.winhey.data.models.Transaction
import com.example.winhey.ui.viewmodel.PlayerMoneyViewModel

class TransactionDetailAdapter(
    private var itemList: List<Transaction>?,
    private val playerMoneyViewModel: PlayerMoneyViewModel
) :
    RecyclerView.Adapter<TransactionDetailAdapter.TransactionDetailViewHolder>() {

    class TransactionDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.userNameTransaction)
        val upiAddress: TextView = itemView.findViewById(R.id.upiAddress)
        val transactionId: TextView = itemView.findViewById(R.id.transactionId)
        val transactionType: TextView = itemView.findViewById(R.id.transactionType)
        val transactionCheck: CheckBox = itemView.findViewById(R.id.transactionCheck)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionDetailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_details_item_card, parent, false)
        return TransactionDetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionDetailViewHolder, position: Int) {
        val item = itemList?.get(position)
        holder.username.text = item?.dateTime
        holder.transactionId.text = item?.transactionID
        holder.transactionType.text = item?.transactionType.toString()
        holder.upiAddress.text = item?.upiID

        if (item?.isVerified == true) {
            holder.transactionCheck.apply {
                isChecked = true
                isClickable = false
                isActivated = false
                visibility = View.GONE
            }
        } else {
            holder.transactionCheck.apply {
                isChecked = false
                isClickable = true
                isActivated = true
            }
        }

        holder.transactionCheck.setOnClickListener {
            if (item?.isVerified == false) {
                playerMoneyViewModel.updateTransactionEntry(
                    Transaction(
                        userID = item.userID,
                        name = item.name,
                        amount = item.amount,
                        email = item.email,
                        transactionID = item.transactionID,
                        upiID = item.upiID,
                        transactionType = item.transactionType,
                        dateTime = item.dateTime,
                        isVerified = true
                    )
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }
}