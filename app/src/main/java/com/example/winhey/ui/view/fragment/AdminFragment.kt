package com.example.winhey.ui.view.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.winhey.R
import com.example.winhey.data.models.Common
import com.example.winhey.data.models.Constants
import com.example.winhey.data.models.Player
import com.example.winhey.data.models.Resource
import com.example.winhey.data.models.Status
import com.example.winhey.data.models.UserType
import com.example.winhey.data.remote.firebase.FirebaseHelper
import com.example.winhey.databinding.FragmentAdminBinding
import com.example.winhey.ui.adapter.UserDetailAdapter
import com.example.winhey.ui.viewmodel.AdminViewModel
import com.example.winhey.ui.viewmodel.AuthViewModel
import com.example.winhey.ui.viewmodel.MainViewModel
import com.example.winhey.utils.PreferencesUtil
import com.example.winhey.utils.WinHeyUtil

class AdminFragment : Fragment() {

    private lateinit var binding: FragmentAdminBinding
    private lateinit var userDetailAdapter: UserDetailAdapter
    private val adminViewModel: AdminViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var adminName: String
    private lateinit var adminEmail: String
    private var googleDriveApkUrl: String = ""
    private var apkVersion: String = "1.1"
    private lateinit var qrImageUrl: String
    val TAG = AdminFragment::class.java.name
    val PICK_IMAGE_REQUEST = 101

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminBinding.inflate(inflater)

        binding.btnCreatePlayer.setOnClickListener {
            binding.adminControlledUserDetails.visibility = View.GONE
            binding.containerCreateNewUser.newUserForm.visibility = View.VISIBLE
            binding.loadingIndicator.visibility = View.GONE
            binding.noUserFound.visibility = View.GONE
            binding.errorView.visibility = View.GONE
        }

        binding.adminLogout.setOnClickListener {
            authViewModel.logout()
            authViewModel.authState.observe(viewLifecycleOwner) {
                when (it) {
                    is Resource.Success -> {
                        if (!it.data.isLoggedIn && it.data.userType == UserType.NONE) {
                            findNavController().navigate(
                                R.id.action_adminFragment_to_authFragment,
                                null,
                                NavOptions.Builder().setPopUpTo(R.id.adminFragment, true).build()
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

        authViewModel.currentUser.observe(viewLifecycleOwner) {
            adminName = it?.displayName.toString()
            adminEmail = it?.email.toString()
        }

        binding.addNewQR.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE_REQUEST
            )
        }

        binding.containerCreateNewUser.btnNewUser.setOnClickListener {
            val email = binding.containerCreateNewUser.editTextEmail.text.toString()
            val password = binding.containerCreateNewUser.editTextPassword.text.toString()
            val userName = binding.containerCreateNewUser.editTextUserName.text.toString()
            val initalAmount =
                binding.containerCreateNewUser.editTextUserInitialAmount.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && initalAmount.isNotBlank()) {
                if (initalAmount.toDouble() >= 0.0) {
                    binding.containerCreateNewUser.loadingIndicator.visibility = View.VISIBLE
                    adminViewModel.createPlayer(
                        email = email,
                        password = password,
                        name = userName,
                        initialAmount = initalAmount.toDouble()
                    )

                    adminViewModel.players.observe(viewLifecycleOwner) {
                        when (it) {
                            is Resource.Loading -> {
                                binding.containerCreateNewUser.loadingIndicator.visibility =
                                    View.VISIBLE
                                binding.containerCreateNewUser.errorView.visibility = View.GONE
                            }

                            is Resource.Failure -> {
                                binding.containerCreateNewUser.loadingIndicator.visibility =
                                    View.GONE
                                binding.containerCreateNewUser.errorView.apply {
                                    visibility = View.VISIBLE
                                    text = it.message
                                }
                            }

                            is Resource.Success -> {
                                binding.containerCreateNewUser.loadingIndicator.visibility =
                                    View.GONE
                                binding.containerCreateNewUser.errorView.visibility = View.GONE
                                Toast.makeText(
                                    requireContext(),
                                    "Player created successfully...",
                                    Toast.LENGTH_LONG
                                ).show()
                                binding.containerCreateNewUser.newUserForm.visibility = View.GONE
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "email, password or initial amount can not be empty!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        mainViewModel.common.observe(viewLifecycleOwner) {
            when(it) {
                is Resource.Success -> {
                    if (it.data.currentAppVersion != PreferencesUtil.getAppVersion(requireContext())) {
                        binding.appUpdate.visibility = View.VISIBLE
                    } else {
                        binding.appUpdate.visibility = View.GONE
                    }
                    googleDriveApkUrl = it.data.apkUrl
                    apkVersion = it.data.currentAppVersion
                    Log.d("####", "onCreateView: " + apkVersion)
                    qrImageUrl = it.data.qRImage
                } else -> {}
            }
        }
        binding.appUpdateBtn.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(googleDriveApkUrl))
            startActivity(browserIntent)
        }

        binding.btnUserDetails.setOnClickListener {
            binding.containerCreateNewUser.newUserForm.visibility = View.GONE
            binding.adminControlledUserDetails.visibility = View.VISIBLE
            handleVisibility(Status.LOADING)
            binding.adminControlledUserDetails.layoutManager =
                LinearLayoutManager(binding.root.context)
//            adminViewModel.fetchAllPlayers()
            adminViewModel.players.observe(viewLifecycleOwner) { resource ->
                when (resource) {
                    is Resource.Success -> {
                        binding.noUserFound.visibility = if (resource.data?.size == 0) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
                        userDetailAdapter = UserDetailAdapter(
                            resource.data,
                            object : UserDetailAdapter.CardClickClickListener {
                                override fun onCardClicked(player: Player?, view: View) {
                                    performCardClick(player, view)
                                }
                            })
                        handleVisibility(Status.SUCCESS)
                        binding.adminControlledUserDetails.adapter = userDetailAdapter
                    }

                    is Resource.Failure -> {
                        if (resource.message == Constants.NO_INTERNET_ERROR) {
                            handleVisibility(Status.ERROR, Constants.NO_INTERNET_ERROR)
                        }

                    }

                    is Resource.Loading -> {
                        handleVisibility(Status.LOADING)
                    }
                }
            }
        }
        handleTransactionDetail()

        return binding.root
    }

    private fun handleTransactionDetail() {
        binding.btnTransactionDetails.setOnClickListener {
            findNavController().navigate(
                R.id.action_adminFragment_to_transactionFragment,
                null,
                NavOptions.Builder().setPopUpTo(R.id.adminFragment, true).build()
            )
        }
    }

    private fun performCardClick(player: Player?, view: View) {
        Log.d("####", "performCardClick: " + player?.gameCount)
        val dialog = DialogUserUpdateFragment.newInstance(
            playerId = player?.id,
            totalLoss = player?.totalLost,
            totalWon = player?.totalWon,
            emailId = player?.email,
            userName = player?.name,
            walletBalance = player?.accountBalance,
            gamePlayed = player?.gameCount
        )
        dialog.show(parentFragmentManager, "DialogUserUpdate")
    }

    private fun handleVisibility(status: Status, err: String = "") {
        when (status) {
            Status.LOADING -> {
                binding.loadingIndicator.visibility = View.VISIBLE
                binding.adminControlledUserDetails.visibility = View.GONE
                binding.errorView.visibility = View.GONE
            }

            Status.ERROR -> {
                if (err == Constants.NO_INTERNET_ERROR) {
                    WinHeyUtil.showNoInternetDialog(requireContext())
                }
                binding.adminControlledUserDetails.visibility = View.GONE
                binding.loadingIndicator.visibility = View.GONE
                binding.errorView.apply {
                    visibility = View.VISIBLE
                    text = err
                }
            }

            Status.SUCCESS -> {
                binding.adminControlledUserDetails.visibility = View.VISIBLE
                binding.loadingIndicator.visibility = View.GONE
                binding.errorView.visibility = View.GONE
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            FirebaseHelper.uploadImageToFirebase(data.data!!, object : FirebaseHelper.FirebaseCallback<String> {
                override fun onSuccess(result: String) {
                    mainViewModel.updateCommonData(
                        Common(
                            adminName = adminName,
                            adminEmail = adminEmail,
                            qRImage = result,
                            currentAppVersion = apkVersion,
                            apkUrl = googleDriveApkUrl
                        )
                    )
                    Toast.makeText(
                        context,
                        "QR code updated successfully: $result",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onFailure(error: String) {
                    Toast.makeText(context, "QR code inundation failed: $error", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_navigation, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}