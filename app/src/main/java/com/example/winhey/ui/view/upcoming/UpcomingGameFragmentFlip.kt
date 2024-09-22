package com.example.winhey.ui.view.upcoming

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.winhey.R
import com.example.winhey.databinding.FragmentColorPredictionMultiPlayerBinding

class UpcomingGameFragmentFlip : Fragment() {

    private lateinit var binding: FragmentColorPredictionMultiPlayerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentColorPredictionMultiPlayerBinding.inflate(inflater)
        binding.gameTimer.setBackgroundResource(R.drawable.bg_timer)
        return binding.root
    }
}