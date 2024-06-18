package com.example.winhey.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.winhey.R
import com.example.winhey.data.models.Transaction
import com.example.winhey.data.models.TransactionType
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
        val date: TextView = itemView.findViewById(R.id.date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionDetailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_details_item_card, parent, false)
        return TransactionDetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionDetailViewHolder, position: Int) {
        val item = itemList?.get(position)
        "Name: ${item?.name}".also { holder.username.text = it }
        "Transaction Id:  ${item?.transactionID}".also { holder.transactionId.text = it }
        "${item?.transactionType.toString()}  ${item?.amount.toString()}".also {
            if (item?.transactionType == TransactionType.WITHDRAW) {
                holder.transactionType.setTextColor(Color.RED)
            }
            if (item?.transactionType == TransactionType.DEPOSIT) {
                holder.transactionType.setTextColor(Color.parseColor("#379F7C"))
            }
            holder.transactionType.text = it
        }
        "Upi address: ${item?.upiID}".also { holder.upiAddress.text = it }
        holder.date.text = item?.dateTime

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