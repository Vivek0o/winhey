package com.avfusionapps.winhey.ui.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.avfusionapps.winhey.databinding.FragmentBlockedBinding

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