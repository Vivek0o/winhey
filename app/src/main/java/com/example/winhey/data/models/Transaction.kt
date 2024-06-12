package com.example.winhey.data.models

data class Transaction(
    var userID: String = "",
    var name: String = "",
    var email: String = "",
    var amount: Double = 0.0,
    var transactionID: String = "",
    var upiID: String = "",
    var transactionType: TransactionType = TransactionType.NONE,
    var dateTime: String = " ",
    var isVerified: Boolean = false
)
