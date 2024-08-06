package com.example.winhey.ui.view.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.winhey.R
import com.example.winhey.databinding.FragmentReferralBinding

class ReferralFragment : Fragment() {

    lateinit var binding: FragmentReferralBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReferralBinding.inflate(inflater)

        handleBackNavigation()
        handleReferralButton()

        return binding.root
    }

    private fun handleReferralButton() {
        binding.referButton.setOnClickListener {
            Toast.makeText(context, "Connect With admin to refer", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleBackNavigation() {
        binding.referralToolbar.setNavigationOnClickListener {
            findNavController().navigate(
                R.id.action_referralFragment_to_playerFragment,
                null,
                NavOptions.Builder().setPopUpTo(R.id.referralFragment, true).build()
            )
        }
    }
}
