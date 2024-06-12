package com.example.winhey.ui.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.winhey.R
import com.example.winhey.data.models.Constants
import com.example.winhey.data.models.Resource
import com.example.winhey.data.models.Status
import com.example.winhey.data.models.UserType
import com.example.winhey.databinding.FragmentAuthBinding
import com.example.winhey.ui.viewmodel.AuthViewModel
import com.example.winhey.ui.viewmodel.MainViewModel
import com.example.winhey.utils.WinHeyUtil

class AuthFragment : Fragment() {

    val TAG = AuthFragment::class.java.name
    private lateinit var binding: FragmentAuthBinding
    private val authViewModel: AuthViewModel by viewModels({ requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthBinding.inflate(inflater)

        authViewModel.authState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Failure -> {
                    if (resource.message == Constants.NO_INTERNET_ERROR) {
                        handleVisibility(Status.ERROR, Constants.NO_INTERNET_ERROR)
                    } else {
                        handleVisibility(Status.ERROR, resource.message)
                    }

                    binding.errorView.text = resource.message
                }

                is Resource.Loading -> {
                    handleVisibility(Status.LOADING)
                }

                is Resource.Success -> {
                    resource.data.let {
                        when {
                            it.isLoggedIn && it.userType == UserType.ADMIN ->
                                findNavController().navigate(R.id.action_authFragment_to_adminFragment)

                            it.isLoggedIn && it.userType == UserType.PLAYER -> {
                                findNavController().navigate(R.id.action_authFragment_to_playerFragment)
                            }

                            else -> handleVisibility(Status.SUCCESS)
                        }
                    }
                }
            }
        }

        binding.submitButton.setOnClickListener {
            val email = binding.editTextEmail.text
            val password = binding.editTextPassword.text
            if (email?.isNotEmpty() == true && password?.isNotEmpty() == true) {
                handleVisibility(Status.LOADING)
                authViewModel.login(email.toString(), password.toString())
            } else {
                Toast.makeText(
                    requireContext(),
                    "User Name or Password can not be empty",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        return binding.root
    }

    private fun handleVisibility(status: Status, err: String = "") {
        when (status) {
            Status.LOADING -> {
                binding.loadingIndicator.visibility = View.VISIBLE
                binding.authScreen.visibility = View.VISIBLE
                binding.errorView.visibility = View.GONE
            }

            Status.ERROR -> {
                if (err == Constants.NO_INTERNET_ERROR) {
                    WinHeyUtil.showNoInternetDialog(requireContext())
                }
                binding.loadingIndicator.visibility = View.GONE
                binding.authScreen.visibility = View.VISIBLE
                binding.errorView.visibility = View.VISIBLE
                binding.errorView.text = err
            }

            Status.SUCCESS -> {
                binding.loadingIndicator.visibility = View.GONE
                binding.authScreen.visibility = View.VISIBLE
                binding.errorView.visibility = View.GONE
            }
        }
    }
}