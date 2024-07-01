package com.example.winhey.ui.view.fragment

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.winhey.R
import com.example.winhey.databinding.FragmentBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MoneyBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomSheetBinding

    interface MoneyBottomSheetListener {
        fun onSubmitValue(value: Double)
    }

    private var listener: MoneyBottomSheetListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = try {
            parentFragment as MoneyBottomSheetListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement MoneyBottomSheetListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetBinding.inflate(inflater)
        handleTag()
        return binding.root
    }

    private fun handleTag() {
        val cardClickListener = View.OnClickListener { v ->
            val value = when (v.id) {
                R.id.plus_20 -> 20
                R.id.plus_50 -> 50
                R.id.plus_100 -> 100
                else -> 0
            }
            binding.editTextAmountMore.setText(value.toString())
        }

        binding.plus20.setOnClickListener(cardClickListener)
        binding.plus50.setOnClickListener(cardClickListener)
        binding.plus100.setOnClickListener(cardClickListener)

        binding.amountSubmit.setOnClickListener {
            val inputValue = binding.editTextAmountMore.text.toString().toDouble()
            if (inputValue != null && listener != null) {
                listener?.onSubmitValue(inputValue)
            } else {
                Toast.makeText(context, "Please enter a valid value", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(R.id.bottom_sheet_container)
            bottomSheet?.let {
                val background = ContextCompat.getDrawable(requireContext(), R.drawable.bottom_sheet_background)
                it.background = InsetDrawable(background, 0)
            }
        }
        return dialog
    }

    companion object {
        fun newInstance(): MoneyBottomSheetFragment {
            return MoneyBottomSheetFragment()
        }
    }

}