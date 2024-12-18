package com.avfusionapps.winhey.ui.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.avfusionapps.winhey.data.models.Player
import com.avfusionapps.winhey.databinding.FragmentDialogUserUpdateBinding
import com.avfusionapps.winhey.ui.viewmodel.AdminViewModel

class DialogUserUpdateFragment : DialogFragment() {

    private lateinit var binding: FragmentDialogUserUpdateBinding
    private val viewModel: AdminViewModel by activityViewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDialogUserUpdateBinding.inflate(inflater)

        val emailId = arguments?.getString("emailId") ?: " "
        val totalLoss = arguments?.getDouble("totalLoss") ?: 0.0
        val totalWon = arguments?.getDouble("totalWon") ?: 0.0
        val userName = arguments?.getString("userName") ?: ""
        val userId = arguments?.getString("playerId")
        val walletBalance = arguments?.getDouble("walletBalance") ?: 0.0
        val gamePlayed = arguments?.getInt("gamePlayed") ?: 0

        binding.editTextName.text = "Name: $userName"
        binding.editTextTotalLoss.text = "Total loss: $totalLoss"
        binding.editTextTotalWon.text = "Total won: $totalWon"
        binding.editTextuserEmail.text = "Email: $emailId"
        binding.editTextGameCount.text = "Game Count: $gamePlayed"
        binding.currentBalance.text = "Current Balance: $walletBalance"

        binding.buttonUpdate.setOnClickListener {
            val updatedWalletBalance = binding.editTextWalletBalance.text
            val updatedWinningBalance = binding.editTextWinningBalance.text

            if (updatedWalletBalance != null && updatedWalletBalance.toString()
                    .isNotEmpty() && updatedWalletBalance.toString().toDouble() >= 0
            ) {
                userId?.let {
                    viewModel.updatePlayer(
                        Player(
                            id = it,
                            name = userName,
                            email = emailId,
                            accountBalance = updatedWalletBalance.toString().toDouble(),
                            totalLost = totalLoss,
                            totalWon = totalWon,
                            gameCount = gamePlayed
                        )
                    )
                }
            }

            if (updatedWinningBalance != null && updatedWinningBalance.toString()
                    .isNotEmpty() && updatedWinningBalance.toString().toDouble() >= 0
            ) {
                userId?.let {
                    viewModel.updatePlayer(
                        Player(
                            id = it,
                            name = userName,
                            email = emailId,
                            accountBalance = walletBalance,
                            totalLost = totalLoss,
                            totalWon = updatedWinningBalance.toString().toDouble(),
                            gameCount = gamePlayed
                        )
                    )

                }
            }
            dismiss()
        }

        binding.deleteUpdate.setOnClickListener {
            if (userId != null) {
                viewModel.deletePlayer(userId)
            }
            dismiss()
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }

    companion object {
        fun newInstance(
            playerId: String?,
            totalLoss: Double?,
            totalWon: Double?,
            emailId: String?,
            userName: String?,
            walletBalance: Double?,
            gamePlayed: Int?
        ): DialogUserUpdateFragment {
            val fragment = DialogUserUpdateFragment()
            val args = Bundle()
            args.putString("emailId", emailId)
            args.putString("userName", userName)
            args.putDouble("walletBalance", walletBalance!!)
            args.putDouble("totalWon", totalWon!!)
            args.putDouble("totalLoss", totalLoss!!)
            args.putString("playerId", playerId)
            args.putInt("gamePlayed", gamePlayed!!)

            fragment.arguments = args
            return fragment
        }
    }
}