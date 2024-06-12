package com.example.winhey.ui.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.winhey.R
import com.example.winhey.data.models.Resource
import com.example.winhey.data.models.Transaction
import com.example.winhey.databinding.FragmentTransactionBinding
import com.example.winhey.ui.adapter.TransactionDetailAdapter
import com.example.winhey.ui.viewmodel.PlayerMoneyViewModel

class TransactionFragment : Fragment() {

    private lateinit var binding: FragmentTransactionBinding
    private lateinit var transactionDetailAdapter: TransactionDetailAdapter
    private val playerMoneyViewModel: PlayerMoneyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransactionBinding.inflate(inflater)
        binding.transactionDetails.layoutManager =
            LinearLayoutManager(binding.root.context)
        playerMoneyViewModel.fetchAllTransactions()
        playerMoneyViewModel.transactions.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    val sortedList = sortList(resource.data)
                    transactionDetailAdapter =
                        TransactionDetailAdapter(sortedList, playerMoneyViewModel)
                    binding.transactionDetails.adapter = transactionDetailAdapter
                }

                is Resource.Failure -> {
                    //TODO : Handle Failure
                }

                is Resource.Loading -> {
                    //TODO: Handle Loading
                }
            }
        }

        handleButtonClick()
        return binding.root
    }

    private fun handleButtonClick() {
        binding.transactionDetailToolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_transactionFragment_to_adminFragment)
        }
    }

    private fun sortList(data: List<Transaction>?): List<Transaction>? {
        val sortedList =
            data?.sortedWith(compareBy<Transaction> { it.isVerified }.thenByDescending { it.dateTime })
        return sortedList
    }
}
