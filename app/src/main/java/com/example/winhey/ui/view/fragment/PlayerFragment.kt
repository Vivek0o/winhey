package com.example.winhey.ui.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.winhey.R
import com.example.winhey.data.local.GameItem
import com.example.winhey.data.models.Resource
import com.example.winhey.databinding.FragmentPlayerBinding
import com.example.winhey.ui.PlayerViewModelFactory
import com.example.winhey.ui.viewmodel.AuthViewModel
import com.example.winhey.ui.viewmodel.PlayerViewModel

class PlayerFragment : Fragment() {

    private lateinit var binding: FragmentPlayerBinding
    private lateinit var playerViewModel: PlayerViewModel
    private val authViewModel: AuthViewModel by viewModels({ requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerBinding.inflate(inflater)
        authViewModel.currentUser.value?.uid?.let {
            val factory = PlayerViewModelFactory(requireActivity().application, it)
            playerViewModel = ViewModelProvider(this, factory)[PlayerViewModel::class.java]
        }

        playerViewModel.currentPlayer.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    if (it.data.isBlocked) {
                        findNavController().navigate(R.id.action_playerFragment_to_blockedFragment)
                    }
                }

                else -> {}
            }
        }

        handleGameClick()
        return binding.root
    }

    private fun handleGameClick() {
        binding.colorPredictionCard.setBackgroundResource(R.drawable.background_game_1)
        binding.game2.setBackgroundResource(R.drawable.roulette_game_background)
        binding.game3.setBackgroundResource(R.drawable.ludo_predication_background)

        binding.colorPredictionCard.setOnClickListener {
            findNavController().navigate(R.id.action_playerFragment_to_gameFragment)

        }
    }
}