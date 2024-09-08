package com.example.winhey.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.winhey.R
import com.example.winhey.data.local.WinningInformation

class WinningInformationAdapter(private val items: List<WinningInformation>) :
    RecyclerView.Adapter<WinningInformationAdapter.MaterialCardViewHolder>() {

    class MaterialCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profileImage: ImageView = view.findViewById(R.id.profile_image)
        val nameText: TextView = view.findViewById(R.id.name_text)
        val line1Text: TextView = view.findViewById(R.id.line1_text)
        val line2Text: TextView = view.findViewById(R.id.line2_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialCardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_winning_information, parent, false)
        return MaterialCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: MaterialCardViewHolder, position: Int) {
        val item = items[position]
        // Bind data here
        holder.profileImage.setImageResource(item.profileImageRes)
        holder.nameText.text = item.name
        holder.line1Text.text = item.line1Text
        holder.line2Text.text = item.line2Text
    }

    override fun getItemCount(): Int = items.size
}
