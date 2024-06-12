package com.example.winhey.ui.view.fragment

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.winhey.R
import com.example.winhey.data.models.Player
import com.example.winhey.data.models.Resource
import com.example.winhey.databinding.FragmentColorPredictionGameBinding
import com.example.winhey.ui.PlayerViewModelFactory
import com.example.winhey.ui.viewmodel.AdminViewModel
import com.example.winhey.ui.viewmodel.AuthViewModel
import com.example.winhey.ui.viewmodel.PlayerViewModel
import java.io.IOException

class GameFragment : Fragment(), MoneyBottomSheetFragment.MoneyBottomSheetListener {

    private lateinit var binding: FragmentColorPredictionGameBinding
    private var mediaPlayer: MediaPlayer? = null
    private val authViewModel: AuthViewModel by activityViewModels()
    private val adminViewModel: AdminViewModel by activityViewModels()
    private lateinit var playerViewModel: PlayerViewModel
    private val bottomSheetFragment = MoneyBottomSheetFragment.newInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentColorPredictionGameBinding.inflate(inflater)

        authViewModel.currentUser.value?.uid?.let {
            val factory = PlayerViewModelFactory(application = requireActivity().application, it)
            playerViewModel = ViewModelProvider(this, factory)[PlayerViewModel::class.java]
        }

        handlePlayerData()
        handleMusic()
        handleButtonClick()
        openBottomSheet()
        return binding.root
    }

    private fun handlePlayerData() {
        playerViewModel.currentPlayer.observe(viewLifecycleOwner) { player ->
            when (player) {
                is Resource.Success -> {
                    binding.gameTotalAmount.text = player.data.accountBalance.toString()
                    binding.gameTotalWon.text = player.data.totalWon.toString()
                }

                else -> {}
            }
        }
    }


    private fun handleButtonClick() {
        binding.backBtn.setOnClickListener {
            findNavController().navigate(R.id.action_gameFragment_to_playerFragment)
        }
    }

    private fun handleMusic() {
        try {
            mediaPlayer = MediaPlayer()
            val assetFileDescriptor = context?.assets?.openFd("game_entry_audio.mp3")
            mediaPlayer?.setDataSource(
                assetFileDescriptor?.fileDescriptor,
                assetFileDescriptor?.startOffset ?: 0,
                assetFileDescriptor?.length ?: 0
            )

            mediaPlayer?.prepare()
            mediaPlayer?.start()
            mediaPlayer?.let {
                view?.postDelayed({
                    it.stop()
                    it.release()
                    mediaPlayer = null
                }, 4000) //
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun openBottomSheet() {
        binding.playButton.setOnClickListener {
            bottomSheetFragment.show(childFragmentManager, "MoneyBottomSheetFragment")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onSubmitValue(value: Int) {
        playerViewModel.currentPlayer.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    if (value <= it.data.accountBalance) {
                        childFragmentManager.findFragmentByTag("MoneyBottomSheetFragment")
                            ?.let { fragment ->
                                (fragment as MoneyBottomSheetFragment).dismiss()
                            }
                        binding.playButton.text = "Play"
                        val updatedBalance = it.data.accountBalance - value
                        adminViewModel.updatePlayer(
                            Player(
                                id = it.data.id,
                                name = it.data.name,
                                email = it.data.email,
                                accountBalance = updatedBalance,
                                totalLost = it.data.totalLost,
                                totalWon = it.data.totalWon,
                                gameCount = (it.data.gameCount)++
                            )
                        )
                        Log.e("Vivek", "onSubmitValue: $updatedBalance", )

                    } else {
                        // Show an error
                        Toast.makeText(context, "Insufficient balance", Toast.LENGTH_SHORT).show()
                        childFragmentManager.findFragmentByTag("MoneyBottomSheetFragment")
                            ?.let { fragment ->
                                (fragment as MoneyBottomSheetFragment).dismiss()
                            }
                    }
                }

                else -> {}
            }
        }
    }
}