package com.example.winhey.ui.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.winhey.R
import com.example.winhey.databinding.FragmentUpcomingGameBinding

class UpcomingGameFragment : Fragment() {

    private lateinit var binding: FragmentUpcomingGameBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpcomingGameBinding.inflate(inflater)

        cardUISetUp()
        return binding.root
    }

    private fun cardUISetUp() {
        binding.upcomingGameCard1.setBackgroundResource(R.drawable.card_background)
        binding.upcomingGameCard2.setBackgroundResource(R.drawable.card_background)

        Glide.with(this)
            .asGif()
            .load(R.drawable.play_now_button)
            .into(binding.playNow)

        Glide.with(this)
            .asGif()
            .load(R.drawable.play_now_button)
            .into(binding.playButton2)
    }
}