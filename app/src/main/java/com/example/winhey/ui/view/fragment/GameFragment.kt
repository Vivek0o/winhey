package com.example.winhey.ui.view.fragment

import android.app.AlertDialog
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.winhey.R
import com.example.winhey.data.models.Player
import com.example.winhey.data.models.Resource
import com.example.winhey.databinding.FragmentColorPredictionGameBinding
import com.example.winhey.ui.PlayerViewModelFactory
import com.example.winhey.ui.viewmodel.AdminViewModel
import com.example.winhey.ui.viewmodel.AuthViewModel
import com.example.winhey.ui.viewmodel.GameViewModel
import com.example.winhey.ui.viewmodel.PlayerViewModel
import com.example.winhey.utils.FlipFlopGameUtil
import java.io.IOException

class GameFragment : Fragment(), MoneyBottomSheetFragment.MoneyBottomSheetListener {

    private lateinit var binding: FragmentColorPredictionGameBinding
    private var mediaPlayer: MediaPlayer? = null
    private val authViewModel: AuthViewModel by activityViewModels()
    private val adminViewModel: AdminViewModel by activityViewModels()
    private val gameViewModel: GameViewModel by activityViewModels()
    private var isBalanceUpdated = false
    private lateinit var playerViewModel: PlayerViewModel
    private lateinit var flipFlopGameUtil: FlipFlopGameUtil
    private lateinit var backPressedCallback: OnBackPressedCallback
    private var gameAmount: Double = 0.0
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

        flipFlopGameUtil = FlipFlopGameUtil(
            binding.cardView1,
            binding.cardView2,
            binding.imageView1,
            binding.imageView2,
            requireContext(),
            view,
            binding)

        flipFlopGameUtil.handleCardClick()
        handlePlayerData()
        handleMusic("game_entry_audio.mp3")
        handleButtonClick()
        openBottomSheet()
        return binding.root
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage("Do you really want to go back?")
            .setPositiveButton("Yes") { _, _ ->
                // Navigate back
                findNavController().navigate(
                    R.id.action_gameFragment_to_playerFragment,
                    null, NavOptions.Builder().setPopUpTo(R.id.gameFragment, true).build()
                )
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

    private fun handleMusic(audio: String) {
        try {
            mediaPlayer = MediaPlayer()
            val assetFileDescriptor = context?.assets?.openFd(audio)
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
                flipFlopGameUtil.changeCardBackground()
                bottomSheetFragment.show(childFragmentManager, "MoneyBottomSheetFragment")
            } else {
                if (flipFlopGameUtil.selected) {
                    gameViewModel.gameState.value.let {
                        if (gameAmount != 0.0) {
                            handleMusic("game_playing_audio.mp3")
                            flipFlopGameUtil.rotateCard(gameViewModel, playerViewModel, binding.playButton)
                            binding.playButton.text = "Join"
                            flipFlopGameUtil.selected = false
                        }
                    }
                } else {
                    Toast.makeText(context, "Please select card", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onSubmitValue(value: Double) {
        isBalanceUpdated = false
        playerViewModel.currentPlayer.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    if (!isBalanceUpdated && value <= it.data.accountBalance) {
                        gameAmount = value
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
                        Handler(Looper.getMainLooper()).postDelayed({
                            if (value > it.data.accountBalance && !isBalanceUpdated) {
                                Toast.makeText(context, "Insufficient balance", Toast.LENGTH_SHORT).show()
                            }
                        }, 500)

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

    private fun updateBalance(player: Player, value: Double) {
        // Update player data in ViewModel
        playerViewModel.updateCurrentPLayer(
            Player(
                id = player.id,
                name = player.name,
                email = player.email,
                accountBalance = player.accountBalance - value,
                totalLost = player.totalLost,
                totalWon = player.totalWon,
                gameCount = player.gameCount,
                lossMargin = player.lossMargin,
                winThreshold = player.winThreshold,
                isBlocked = player.isBlocked
            )
        )

        playerViewModel.currentPlayer.observe(this) {
            when(it) {
                is Resource.Success -> {
                    Log.d("$$$$", "updateBalance: ${it.data.lossMargin} : ${it.data.winThreshold}")
                    gameViewModel.joinGame(it.data, value)
                }
                is Resource.Failure -> {
                }
                else -> {

                }
            }
        }
    }
}