package com.avfusionapps.winhey.ui.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.avfusionapps.winhey.R
import com.avfusionapps.winhey.data.models.Constants
import com.avfusionapps.winhey.data.models.Resource
import com.avfusionapps.winhey.data.models.UserType
import com.avfusionapps.winhey.databinding.FragmentPasswordResetBinding
import com.avfusionapps.winhey.ui.viewmodel.AuthViewModel
import com.avfusionapps.winhey.utils.WinHeyUtil

class PasswordResetFragment : Fragment() {

    private lateinit var binding: FragmentPasswordResetBinding
    private val authViewModel: AuthViewModel by viewModels({ requireActivity() })


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPasswordResetBinding.inflate(inflater)

        binding.backNavigation.setOnClickListener {
            findNavController().navigate(
                R.id.action_passwordResetFragment_to_authFragment,
                null,
                NavOptions.Builder().setPopUpTo(null, true).build()
            )
        }

        binding.signUp.setOnClickListener {
            findNavController().navigate(
                R.id.action_passwordResetFragment_to_authFragment,
                null,
                NavOptions.Builder().setPopUpTo(null, true).build()
            )
        }

        forgotPasswordButtonClick()
        return binding.root
    }

    private fun forgotPasswordButtonClick() {
        binding.btnSendResetLink.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()
            if (email.isNotEmpty()) {
                authViewModel.sendPasswordResetEmail(email = email)
                authViewModel.resetPassword.observe(viewLifecycleOwner) {
                    when(it) {
                        is Resource.Success -> {
                            binding.loadingIndicator.visibility = View.GONE
                            Toast.makeText(context, "If your email is available in our system," +
                                    " you will receive the recovery link",
                                Toast.LENGTH_SHORT).show()
                            findNavController().navigate(
                                R.id.action_passwordResetFragment_to_authFragment,
                                null,
                                NavOptions.Builder().setPopUpTo(null, true).build()
                            )
                        }

                        is Resource.Failure -> {
                            binding.loadingIndicator.visibility = View.GONE
                            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                        }
                        is Resource.Loading -> {
                            binding.loadingIndicator.visibility = View.VISIBLE
                        }
                    }
                }
            } else {
                println("Please enter a valid email address")
            }
        }
    }
}