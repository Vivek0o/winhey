package com.example.winhey.ui.view.fragment

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.winhey.R
import com.example.winhey.data.models.Constants
import com.example.winhey.data.models.Player
import com.example.winhey.data.models.Resource
import com.example.winhey.data.models.Status
import com.example.winhey.data.models.UserType
import com.example.winhey.data.remote.api.AvatarApiService
import com.example.winhey.data.repository.AvatarRepository
import com.example.winhey.databinding.FragmentPlayerBinding
import com.example.winhey.databinding.FragmentProfileBinding
import com.example.winhey.ui.PlayerViewModelFactory
import com.example.winhey.ui.adapter.UserDetailAdapter
import com.example.winhey.ui.viewmodel.AdminViewModel
import com.example.winhey.ui.viewmodel.AuthViewModel
import com.example.winhey.ui.viewmodel.AvatarViewModel
import com.example.winhey.ui.viewmodel.AvatarViewModelFactory
import com.example.winhey.ui.viewmodel.PlayerViewModel
import com.example.winhey.utils.WinHeyUtil
import java.io.File

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val authViewModel: AuthViewModel by activityViewModels()
    private lateinit var playerViewModel: PlayerViewModel
    private lateinit var viewModel: AvatarViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater)

        authViewModel.currentUser.value?.uid?.let {
            val factory = PlayerViewModelFactory(application = requireActivity().application, it)
            playerViewModel = ViewModelProvider(this, factory)[PlayerViewModel::class.java]
        }

        val apiService = AvatarApiService.create()
        val repository = AvatarRepository(apiService, requireContext())
        val factory = AvatarViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(AvatarViewModel::class.java)

        handleAvatar()
        handleButtonClick()
        handleProfileData()

        return binding.root
    }

    private fun handleAvatar() {
        viewModel.avatar.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // Show loading indicator
                }
                is Resource.Success -> {
                    val imagePath = resource.data
                    Glide.with(this)
                        .load(imagePath)
                        .into(binding.profileImage)
                }

                is Resource.Failure -> {
                    Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.fetchAvatar()    }

    private fun handleButtonClick() {
        binding.paymentGateway.setOnClickListener {
            findNavController().navigate(
                R.id.action_profileFragment_to_moneyFragment,
                null,
                NavOptions.Builder().setPopUpTo(R.id.profileFragment, true).build()
            )
        }

        binding.referralGateway.setOnClickListener {
            findNavController().navigate(
                R.id.action_profileFragment_to_referralFragment,
                null,
                NavOptions.Builder().setPopUpTo(R.id.profileFragment, true).build()
            )
        }

        binding.profileToolbar.setNavigationOnClickListener {
            findNavController().navigate(
                R.id.action_profileFragment_to_playerFragment,
                null,
                NavOptions.Builder().setPopUpTo(R.id.profileFragment, true).build()
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleProfileData() {
        playerViewModel.fetchCurrentPlayer()

        playerViewModel.currentPlayer.observe(viewLifecycleOwner) {
            when(it) {
                is Resource.Success -> {
                    if (it.data.isBlocked) {
                        findNavController().navigate(R.id.blockedFragment)
                    }
                    setupVisibility(Status.SUCCESS)
                    binding.userName.text = it.data.name
                    binding.profileEmail.text = it.data.email
                    binding.walletText.text = "Wallet: ${it.data.accountBalance}"
                    binding.totalWon.text = "Total won: ${it.data.totalWon}"
                    binding.totalLost.text = "Total lost: ${it.data.totalLost}"
                }

                is Resource.Loading -> {
                    setupVisibility(Status.LOADING)
                }

                is Resource.Failure -> {
                    setupVisibility(Status.ERROR, it.message)
                }
            }
        }

        binding.logout.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        binding.loadingIndicator.visibility = View.VISIBLE
        authViewModel.logout()
        authViewModel.authState.observe(viewLifecycleOwner) {
            when(it) {
                is Resource.Success -> {
                    if (!it.data.isLoggedIn && it.data.userType == UserType.NONE) {
                        findNavController().navigate(
                            R.id.action_profileFragment_to_authFragment,
                            null,
                            NavOptions.Builder().setPopUpTo(R.id.profileFragment, true).build()
                        )
                    }
                }

                is Resource.Failure -> {
                    binding.loadingIndicator.visibility = View.GONE
                    if (it.message == Constants.NO_INTERNET_ERROR) {
                        WinHeyUtil.showNoInternetDialog(requireContext())
                    }
                }
                is Resource.Loading -> {
                    binding.loadingIndicator.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setupVisibility(status: Status, message: String = "") {
        when(status) {
            Status.SUCCESS -> {
                binding.loadingIndicator.visibility = View.GONE
                binding.errorView.visibility = View.GONE
            }
            Status.LOADING -> {
                binding.loadingIndicator.visibility = View.VISIBLE
                binding.errorView.visibility = View.GONE
            }
            Status.ERROR -> {
                binding.loadingIndicator.visibility = View.GONE
                binding.errorView.visibility = View.VISIBLE
                binding.errorView.text = message
                if (message == Constants.NO_INTERNET_ERROR) {
                    WinHeyUtil.showNoInternetDialog(requireContext())
                }
            }
        }
    }
}