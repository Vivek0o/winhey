package com.avfusionapps.winhey.ui.view.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.avfusionapps.winhey.R
import com.avfusionapps.winhey.data.models.Resource
import com.avfusionapps.winhey.databinding.FragmentReferralBinding
import com.avfusionapps.winhey.ui.viewmodel.MainViewModel

class ReferralFragment : Fragment() {

    lateinit var binding: FragmentReferralBinding
    private var googleDriveApkUrl: String = ""

    private val mainViewModel: MainViewModel by viewModels({ requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReferralBinding.inflate(inflater)
        mainViewModel.common.observe(viewLifecycleOwner) {
            when(it) {
                is Resource.Success -> {
                    googleDriveApkUrl = it.data.apkUrl
                } else -> {}
            }
        }

        handleBackNavigation()
        handleReferralButton()

        return binding.root
    }

    private fun handleReferralButton() {
        binding.referButton.setOnClickListener {
            if (googleDriveApkUrl.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, googleDriveApkUrl)
                }
                // Start the activity to share the message
                startActivity(Intent.createChooser(intent, "Share via"))
            }
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
