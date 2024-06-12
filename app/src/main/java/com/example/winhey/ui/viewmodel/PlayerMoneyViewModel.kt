package com.example.winhey.ui.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.winhey.data.models.Constants
import com.example.winhey.data.models.Transaction
import com.example.winhey.data.models.Resource
import com.example.winhey.data.remote.firebase.FirebaseHelper

class PlayerMoneyViewModel(application: Application) : BaseViewModel(application) {
    private val _transactions = MutableLiveData<Resource<List<Transaction>?>>()
    val transactions: LiveData<Resource<List<Transaction>?>>
        get() = _transactions

    init {
        fetchAllTransactions()
    }

    fun fetchAllTransactions() {
        _transactions.value = Resource.Loading(getCurrentTransactionListLocally())
        checkInternetAndPerformAction(
            action = {
                FirebaseHelper.fetchAllTransactions(object :
                    FirebaseHelper.FirebaseCallback<List<Transaction>> {
                    override fun onSuccess(result: List<Transaction>) {
                        _transactions.value = Resource.Success(result)
                    }

                    override fun onFailure(error: String) {
                        _transactions.value =
                            Resource.Failure(error, getCurrentTransactionListLocally())
                    }
                })
            },
            failureAction = {
                _transactions.value = Resource.Failure(
                    Constants.NO_INTERNET_ERROR,
                    getCurrentTransactionListLocally()
                )
            }
        )
    }

    fun addTransaction(transaction: Transaction) {
        _transactions.value = Resource.Loading(getCurrentTransactionListLocally())
        checkInternetAndPerformAction(
            action = {
                FirebaseHelper.addTransactionEntry(
                    transaction,
                    object : FirebaseHelper.FirebaseCallback<Boolean> {
                        override fun onSuccess(result: Boolean) {
                            val currentTransactions = getCurrentTransactionListLocally()
                            currentTransactions.add(transaction)
                            _transactions.value = Resource.Success(currentTransactions)
                        }

                        override fun onFailure(error: String) {
                            _transactions.value =
                                Resource.Failure(
                                    message = error,
                                    data = getCurrentTransactionListLocally()
                                )
                            Toast.makeText(
                                getApplication(),
                                "$error : Transaction created but could not add in database",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            },
            failureAction = {
                _transactions.value = Resource.Failure(
                    Constants.NO_INTERNET_ERROR,
                    getCurrentTransactionListLocally()
                )
            }
        )
    }

    fun updateTransactionEntry(transaction: Transaction) {
        checkInternetAndPerformAction(
            action = {
                FirebaseHelper.updateTransactionEntry(
                    transaction,
                    object : FirebaseHelper.FirebaseCallback<Boolean> {
                        override fun onSuccess(result: Boolean) {
                            val currentTransactions = getCurrentTransactionListLocally()
                            val index =
                                currentTransactions.indexOfFirst {
                                    "${it.userID}-${it.dateTime}-${it.name}" == "${transaction.userID}-${transaction.dateTime}-${transaction.name}"
                                }

                            if (index != -1) {
                                currentTransactions[index] = transaction
                                _transactions.value = Resource.Success(currentTransactions)
                                Toast.makeText(
                                    getApplication(),
                                    "Updated...",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                _transactions.value = Resource.Failure(
                                    message = "Entry could not found locally",
                                    data = getCurrentTransactionListLocally()
                                )
                                Toast.makeText(
                                    getApplication(),
                                    "Entry could not found locally",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        override fun onFailure(error: String) {
                            _transactions.value =
                                Resource.Failure(
                                    message = error,
                                    data = getCurrentTransactionListLocally()
                                )
                            Toast.makeText(
                                getApplication(),
                                "$error: Could not update player",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    })
            },
            failureAction = {
                _transactions.value =
                    Resource.Failure(Constants.NO_INTERNET_ERROR, getCurrentTransactionListLocally())
            }
        )

    }

    private fun getCurrentTransactionListLocally() =
        if (_transactions.value != null && _transactions.value is Resource.Success) {
            (_transactions.value as Resource.Success<List<Transaction>>).data.toMutableList()
        } else mutableListOf()
}