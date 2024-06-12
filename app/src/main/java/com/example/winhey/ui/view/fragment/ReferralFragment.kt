package com.example.winhey.ui.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        return binding.root
    }

    private fun handleBackNavigation() {
        binding.referralToolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_referralFragment_to_playerFragment)
        }
    }
}
