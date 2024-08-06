package com.example.winhey.ui.view.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.winhey.R
import com.example.winhey.data.local.GameItem
import com.example.winhey.data.models.Resource
import com.example.winhey.databinding.FragmentPlayerBinding
import com.example.winhey.ui.PlayerViewModelFactory
import com.example.winhey.ui.viewmodel.AuthViewModel
import com.example.winhey.ui.viewmodel.MainViewModel
import com.example.winhey.ui.viewmodel.PlayerViewModel
import com.example.winhey.utils.PreferencesUtil

class PlayerFragment : Fragment() {

    private lateinit var binding: FragmentPlayerBinding
    private lateinit var playerViewModel: PlayerViewModel
    private val authViewModel: AuthViewModel by viewModels({ requireActivity() })
    private val mainViewModel: MainViewModel by viewModels({ requireActivity() })
    private lateinit var googleDriveApkUrl: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerBinding.inflate(inflater)
        authViewModel.currentUser.value?.uid?.let {
            val factory = PlayerViewModelFactory(requireActivity().application, it)
            playerViewModel = ViewModelProvider(this, factory)[PlayerViewModel::class.java]
        }

        mainViewModel.common.observe(viewLifecycleOwner) {
            when(it) {
                is Resource.Success -> {
                    if (it.data.currentAppVersion != PreferencesUtil.getAppVersion(requireContext())) {
                        googleDriveApkUrl = it.data.apkUrl
                        binding.appUpdate.visibility = View.VISIBLE
                    } else {
                        binding.appUpdate.visibility = View.GONE
                    }
                } else -> {}
            }
        }
        binding.appUpdateBtn.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(googleDriveApkUrl))
            startActivity(browserIntent)
        }

        playerViewModel.currentPlayer.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    if (it.data.isBlocked) {
                        findNavController().navigate(
                            R.id.action_playerFragment_to_blockedFragment,
                            null,
                            NavOptions.Builder().setPopUpTo(R.id.playerFragment, true).build()
                        )
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