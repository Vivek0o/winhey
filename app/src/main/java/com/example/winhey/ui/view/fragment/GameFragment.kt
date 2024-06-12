package com.example.winhey.ui.view.fragment

import android.app.AlertDialog
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
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
import com.example.winhey.utils.FlipFlopGameUtil
import java.io.IOException

class GameFragment : Fragment(), MoneyBottomSheetFragment.MoneyBottomSheetListener {

    private lateinit var binding: FragmentColorPredictionGameBinding
    private var mediaPlayer: MediaPlayer? = null
    private val authViewModel: AuthViewModel by activityViewModels()
    private val adminViewModel: AdminViewModel by activityViewModels()
    private var isBalanceUpdated = false
    private lateinit var playerViewModel: PlayerViewModel
    private lateinit var backPressedCallback: OnBackPressedCallback
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


        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitConfirmationDialog()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            backPressedCallback
        )

        handlePlayerData()
        handleMusic()
        handleButtonClick()
        openBottomSheet()
        return binding.root
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage("Do you really want to go back?")
            .setPositiveButton("Yes") { _, _ ->
                // Navigate back
                findNavController().navigate(R.id.action_gameFragment_to_playerFragment)
            }
            .setNegativeButton("No", null)
            .show()
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
            showExitConfirmationDialog()
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
            if (binding.playButton.text == "Join") {
                FlipFlopGameUtil().changeCardBackground(binding.imageView1, binding.imageView2)
                bottomSheetFragment.show(childFragmentManager, "MoneyBottomSheetFragment")
            } else {
                FlipFlopGameUtil().rotateCard(
                    binding.cardView1,
                    binding.cardView2,
                    binding.imageView1,
                    binding.imageView2
                )
                binding.playButton.text = "Join"
            }
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
                    if (!isBalanceUpdated && value <= it.data.accountBalance) {
                        // Update balance once
                        updateBalance(it.data, value)
                        childFragmentManager.findFragmentByTag("MoneyBottomSheetFragment")
                            ?.let { fragment ->
                                (fragment as MoneyBottomSheetFragment).dismiss()
                            }
                        binding.playButton.text = "Play"

                        // Set the flag to true
                        isBalanceUpdated = true
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

    // Reset the flag when the fragment is recreated
    override fun onResume() {
        super.onResume()
        isBalanceUpdated = false
    }

    private fun updateBalance(player: Player, value: Int) {
        val newBalance = player.accountBalance - value
        player.accountBalance = newBalance

        // Update player data in ViewModel
        adminViewModel.updatePlayer(
            Player(
                id = player.id,
                name = player.name,
                email = player.email,
                accountBalance = player.accountBalance,
                totalLost = player.totalLost,
                totalWon = player.totalWon,
                gameCount = (player.gameCount)++
            )
        )
    }
}