package com.example.winhey.ui.view.fragment

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.winhey.R
import com.example.winhey.data.models.Constants
import com.example.winhey.data.models.Player
import com.example.winhey.data.models.Resource
import com.example.winhey.data.models.Status
import com.example.winhey.data.models.Transaction
import com.example.winhey.data.models.TransactionType
import com.example.winhey.databinding.FragmentMoneyPlayerBinding
import com.example.winhey.ui.PlayerMoneyViewModelFactory
import com.example.winhey.ui.PlayerViewModelFactory
import com.example.winhey.ui.viewmodel.AuthViewModel
import com.example.winhey.ui.viewmodel.MainViewModel
import com.example.winhey.ui.viewmodel.PlayerMoneyViewModel
import com.example.winhey.ui.viewmodel.PlayerViewModel
import com.example.winhey.utils.WinHeyUtil
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MoneyFragmentPlayer : Fragment() {

    private lateinit var binding: FragmentMoneyPlayerBinding
    private lateinit var playerMoneyViewModel: PlayerMoneyViewModel
    private lateinit var currentPlayer: Player
    private val authViewModel: AuthViewModel by viewModels({ requireActivity() })
    private val mainViewModel: MainViewModel by viewModels({ requireActivity() })


    private lateinit var playerViewModel: PlayerViewModel
    private var withdrawalAmount = 0.0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoneyPlayerBinding.inflate(inflater)

        authViewModel.currentUser.value?.uid?.let {
            val factory = PlayerViewModelFactory(requireActivity().application, it)
            playerViewModel = ViewModelProvider(this, factory)[PlayerViewModel::class.java]
        }

        mainViewModel.common.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    val uri = Uri.parse(it.data.qRImage)
                    Glide.with(this)
                        .load(uri)
                        .into(binding.qrCode)
                }

                is Resource.Loading -> {
                    showToast("Loading... ")
                }

                is Resource.Failure -> {
                    showToast("Could not load qr code: ${it.message}")
                }
            }
        }

        playerViewModel.currentPlayer.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    currentPlayer = it.data
                    "Current Balance: ${it.data.accountBalance}".also {
                        binding.userCurrentBalance.text = it
                    }
                    "Winning Amount: ${it.data.totalWon}".also {
                        binding.userWinningAmount.text = it
                    }
                    binding.containerWithdraw.withdrawalAmount.text =
                        getWithdrawalAmount(it.data.accountBalance, it.data.totalWon).toString()
                    binding.containerWithdraw.currentBalanceAmount.text =
                        it.data.accountBalance.toString()
                    binding.containerWithdraw.winningBalanceAmount.text =
                        (it.data.totalWon * 0.20).toString()
                }

                is Resource.Loading -> {
                    showToast("Loading... ")
                }

                is Resource.Failure -> {
                    showToast("Could not fetch current user: ${it.message}")
                }
            }
        }

        val factory = PlayerMoneyViewModelFactory(requireActivity().application)
        playerMoneyViewModel = ViewModelProvider(this, factory)[PlayerMoneyViewModel::class.java]

        handleButtonClick()
        return binding.root

    }

    private fun getWithdrawalAmount(accountBalance: Double, winingBalance: Double): Double {
        var wonAmount = 0.0
        if (winingBalance > 0) {
           wonAmount = winingBalance * 0.20
        }
        withdrawalAmount =  accountBalance + wonAmount
        return withdrawalAmount
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleButtonClick() {
        binding.addMoney.setOnClickListener {
            binding.withdrawMoneyLayout.visibility = View.GONE
            binding.addMoneyLayout.visibility = View.VISIBLE
        }
        binding.addTransaction.setOnClickListener {
            addTransactionEntry(TransactionType.DEPOSIT)
        }

        binding.withdrawMoney.setOnClickListener {
            binding.addMoneyLayout.visibility = View.GONE
            binding.withdrawMoneyLayout.visibility = View.VISIBLE
        }

        binding.containerWithdraw.withdrawButton.setOnClickListener {
            addTransactionEntry(TransactionType.WITHDRAW)
        }

        binding.moneyToolbar.setNavigationOnClickListener {
            findNavController().navigate(
                R.id.action_moneyFragment_to_playerFragment,
                null,
                NavOptions.Builder().setPopUpTo(R.id.moneyFragment, true).build()
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addTransactionEntry(type: TransactionType) {
        val transaction: Transaction = when (type) {
            TransactionType.DEPOSIT -> {
                val transactionID = binding.editTextTransactionID.text.toString()
                val amount = binding.editTextAmount.text.toString()
                if (transactionID.isNotEmpty() && amount.isNotEmpty()) {
                    Transaction(
                        userID = currentPlayer.id,
                        name = currentPlayer.name,
                        email = currentPlayer.email,
                        amount = amount.toDouble(),
                        transactionID = transactionID,
                        dateTime = LocalDateTime.now()
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        transactionType = type
                    )
                } else {
                    showToast("transactionID/ amount can not be empty...")
                    return
                }
            }

            TransactionType.WITHDRAW -> {
                val amount = binding.containerWithdraw.editTextAmountWithdrawal.text.toString()
                val upiID = binding.containerWithdraw.editTextUserUPI.text.toString()
                if (upiID.isNotEmpty() && amount.isNotEmpty() && amount.toDouble() < withdrawalAmount) {
                    Transaction(
                        userID = currentPlayer.id,
                        name = currentPlayer.name,
                        email = currentPlayer.email,
                        amount = amount.toDouble(),
                        upiID = upiID,
                        dateTime = LocalDateTime.now()
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        transactionType = type
                    )
                } else {
                    showToast("upiID/ amount can not be empty or insufficient balance...")
                    return
                }
            }

            TransactionType.NONE -> {
                showToast("Invalid transaction type...")
                return
            }
        }

        playerMoneyViewModel.addTransaction(transaction)
        playerMoneyViewModel.transactions.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    handleVisibility(status = Status.SUCCESS, type = type)
                }

                is Resource.Failure -> {
                    handleVisibility(status = Status.ERROR, err = it.message, type = type)
                }

                is Resource.Loading -> {
                    handleVisibility(status = Status.LOADING, type = type)
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun handleVisibility(status: Status, type: TransactionType, err: String = "") {
        when (status) {
            Status.LOADING -> {
                if (type == TransactionType.DEPOSIT) {
                    binding.loadingIndicator.visibility = View.VISIBLE
                    binding.errorView.visibility = View.GONE
                } else {
                    binding.containerWithdraw.loadingIndicator.visibility = View.VISIBLE
                    binding.containerWithdraw.errorView.visibility = View.GONE
                }
            }

            Status.ERROR -> {
                if (err == Constants.NO_INTERNET_ERROR) {
                    WinHeyUtil.showNoInternetDialog(requireContext())
                }
                if (type == TransactionType.DEPOSIT) {
                    binding.loadingIndicator.visibility = View.GONE
                    binding.errorView.visibility = View.GONE
                } else {
                    binding.containerWithdraw.loadingIndicator.visibility = View.GONE
                    binding.containerWithdraw.errorView.visibility = View.VISIBLE
                }

            }

            Status.SUCCESS -> {
                if (type == TransactionType.DEPOSIT) {
                    binding.loadingIndicator.visibility = View.GONE
                    binding.errorView.visibility = View.GONE
                } else {
                    binding.containerWithdraw.loadingIndicator.visibility = View.GONE
                    binding.containerWithdraw.errorView.visibility = View.GONE
                }
                Log.d("####", "handleVisibility:call $status $type ")
                showToast("Transaction updated, it will take 1-2hrs to update in wallet")
                findNavController().navigate(
                    R.id.action_moneyFragment_to_profileFragment,
                    null,
                    NavOptions.Builder().setPopUpTo(R.id.moneyFragment, true).build()
                )
            }
        }
    }
}
