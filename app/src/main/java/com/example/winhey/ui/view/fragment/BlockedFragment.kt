package com.example.winhey.ui.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.winhey.R
import com.example.winhey.data.models.Resource
import com.example.winhey.data.models.UserType
import com.example.winhey.databinding.FragmentBlockedBinding
import com.example.winhey.ui.PlayerViewModelFactory
import com.example.winhey.ui.viewmodel.AuthViewModel
import com.example.winhey.ui.viewmodel.PlayerViewModel
import com.example.winhey.utils.WinHeyUtil

class BlockedFragment : Fragment() {

    private lateinit var binding: FragmentBlockedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBlockedBinding.inflate(inflater)

        return binding.root
    }
}