package com.example.winhey.ui.view.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.winhey.R
import com.example.winhey.data.local.GameItem
import com.example.winhey.data.local.WinningInformation
import com.example.winhey.data.models.Resource
import com.example.winhey.data.remote.api.AvatarApiService
import com.example.winhey.data.repository.AvatarRepository
import com.example.winhey.databinding.FragmentPlayerBinding
import com.example.winhey.ui.PlayerViewModelFactory
import com.example.winhey.ui.adapter.WinningInformationAdapter
import com.example.winhey.ui.viewmodel.AuthViewModel
import com.example.winhey.ui.viewmodel.AvatarViewModel
import com.example.winhey.ui.viewmodel.AvatarViewModelFactory
import com.example.winhey.ui.viewmodel.MainViewModel
import com.example.winhey.ui.viewmodel.PlayerViewModel
import com.example.winhey.utils.LocalVariables
import com.example.winhey.utils.NameProvider
import com.example.winhey.utils.PreferencesUtil

class PlayerFragment : Fragment() {

    private lateinit var binding: FragmentPlayerBinding
    private lateinit var playerViewModel: PlayerViewModel
    private val authViewModel: AuthViewModel by viewModels({ requireActivity() })
    private val mainViewModel: MainViewModel by viewModels({ requireActivity() })
    private lateinit var viewModel: AvatarViewModel
    private lateinit var googleDriveApkUrl: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerBinding.inflate(inflater)

        // Avatar for top-performer
        val apiService = AvatarApiService.create()
        val repository = AvatarRepository(apiService, requireContext())
        val factory = AvatarViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(AvatarViewModel::class.java)

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

        handleAvatarForPerformer(binding.topPerformerView.firstPlayerImage, 1)
        handleAvatarForPerformer(binding.topPerformerView.secondPlayerImage, 2)
        handleAvatarForPerformer(binding.topPerformerView.thirdPlayerImage, 3)
        handleWinningInformation()
        handleGameClick()
        return binding.root
    }

    private fun handleAvatarForPerformer(view: ImageView, avatarIndex: Int) {
        val avatarLiveData = when (avatarIndex) {
            1 -> viewModel.winnerFirst
            2 -> viewModel.winnnerSecond
            3 -> viewModel.winnerThird
            else -> throw IllegalArgumentException("Invalid avatar index")
        }

        avatarLiveData.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // Show loading indicator if necessary
                }
                is Resource.Success -> {
                    val imagePath = resource.data
                    Log.d("####", "handleAvatarForPerformer: Avatar$avatarIndex: $imagePath")
                    Glide.with(this)
                        .load(imagePath)
                        .circleCrop()
                        .into(view)
                }
                is Resource.Failure -> {
                    Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun handleWinningInformation() {
        binding.winningInformationRecyclerView.layoutManager = LinearLayoutManager(context)

        // To handle the data for winning information para.
        val shuffledWinningName = NameProvider.names.shuffled()
        val shuffledWinningAmount = NameProvider.winningAmount.shuffled()

        // Prepare your data
        val items = listOf(
            WinningInformation(R.drawable.ic_profile_holder, shuffledWinningName[0],
                R.drawable.background_game_1, shuffledWinningAmount[0], LocalVariables.RECEIVED_AMOUNT),
            WinningInformation(R.drawable.ic_cat_placeholder, shuffledWinningName[1],
                R.drawable.background_game_1, shuffledWinningAmount[1],  LocalVariables.RECEIVED_AMOUNT),
            WinningInformation(R.drawable.ic_profile_holder, shuffledWinningName[2],
                R.drawable.background_game_1, shuffledWinningAmount[2],  LocalVariables.RECEIVED_AMOUNT),
            WinningInformation(R.drawable.ic_cat_placeholder, shuffledWinningName[3],
                R.drawable.background_game_1, shuffledWinningAmount[3],  LocalVariables.RECEIVED_AMOUNT),
            WinningInformation(R.drawable.ic_cat_placeholder, shuffledWinningName[4],
                R.drawable.background_game_1, shuffledWinningAmount[4],  LocalVariables.RECEIVED_AMOUNT),
            WinningInformation(R.drawable.ic_cat_placeholder, shuffledWinningName[5],
                R.drawable.background_game_1, shuffledWinningAmount[5],  LocalVariables.RECEIVED_AMOUNT),

            )

        val adapter = WinningInformationAdapter(items)
        binding.winningInformationRecyclerView.adapter = adapter

        // handling the top performer.
        val shuffledNames = NameProvider.names.shuffled()
        binding.topPerformerView.firstPlayerName.text = shuffledNames[0]
        binding.topPerformerView.secondPlayerName.text = shuffledNames[1]
        binding.topPerformerView.thirdPlayerName.text = shuffledNames[2]
        binding.fourthPerformer.nameText.text = shuffledNames[3]
        binding.fifthPerformer.nameText.text = shuffledNames[4]
        binding.fourthPerformer.line1Text.text = "Rs. 35000"
        binding.fourthPerformer.line2Text.text = "4th Position"
        binding.fifthPerformer.line1Text.text = "Rs. 33000"
        binding.fifthPerformer.line2Text.text = "5th Position"

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