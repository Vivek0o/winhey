package com.example.winhey.ui.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
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
        handleCardClick()
        return binding.root
    }

    private fun handleCardClick() {
        binding.upcomingGameCard1Mint.setOnClickListener {
            findNavController().navigate(
                R.id.action_upcomingGameFragment_to_upcomingGameFragmentFlip,
                null,
                NavOptions.Builder().setPopUpTo(R.id.upcomingGameFragment, true).build()
            )
        }

        binding.upcomingGameCard2Mint.setOnClickListener {
            Toast.makeText(context, "Hello2", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cardUISetUp() {
        binding.upcomingGameCard1Mint.setBackgroundResource(R.drawable.card_background)
        binding.upcomingGameCard2Mint.setBackgroundResource(R.drawable.card_background)

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