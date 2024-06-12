package com.example.winhey.data.remote.firebase

import android.util.Log
import com.example.winhey.data.models.Common
import com.example.winhey.data.models.Player
import com.example.winhey.data.models.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object FirebaseHelper {
    val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val playersRef = database.getReference("players")
    private val transactionsRef = database.getReference("transactions")
    private val commonRef = database.getReference("common")

    interface FirebaseCallback<T> {
        fun onSuccess(result: T)
        fun onFailure(error: String)
    }

    fun signUp(email: String, password: String, callback: FirebaseCallback<FirebaseUser?>) {
        try {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback.onSuccess(task.result.user)
                    } else {
                        callback.onFailure(task.exception?.message ?: "Unknown error")
                    }
                }
                .addOnFailureListener { e ->
                    callback.onFailure(e.message ?: "Unknown error")
                }
        } catch (e: Exception) {
            callback.onFailure(e.message ?: "Unknown error")
        }
    }


    fun login(email: String, password: String, callback: FirebaseCallback<FirebaseUser?>) {
        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback.onSuccess(auth.currentUser)
                    } else {
                        callback.onFailure(task.exception?.message ?: "Unknown error")
                    }
                }
                .addOnFailureListener { e ->
                    callback.onFailure(e.message ?: "Unknown error")
                }
        } catch (e: Exception) {
            callback.onFailure(e.message ?: "Unknown error")
        }
    }

    fun logout(callback: FirebaseCallback<Boolean>) {
        try {
            auth.signOut()
            callback.onSuccess(true)
        } catch (e: Exception) {
            callback.onFailure(e.message ?: "Unknown error")
        }
    }


    fun fetchAllPlayers(callback: FirebaseCallback<List<Player>>) {
        try {
            playersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val players = mutableListOf<Player>()
                    for (userSnapshot in snapshot.children) {
                        val player = userSnapshot.getValue(Player::class.java)
                        player?.let { players.add(player) }
                    }
                    callback.onSuccess(players)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback.onFailure(error.message)
                }
            })
        } catch (e: Exception) {
            callback.onFailure(e.message ?: "Unknown error")
        }
    }

    fun fetchCurrentPlayerData(playerId: String, callback: FirebaseCallback<Player>) {
        try {
            playersRef.child(playerId).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val player = snapshot.getValue(Player::class.java)
                    if (player != null) {
                        callback.onSuccess(player)
                    } else {
                        callback.onFailure("Could not find player")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback.onFailure(error.message)
                }

            })
        } catch (e: Exception) {
            callback.onFailure(e.message ?: "Unknown error")
        }
    }

    fun addPlayer(player: Player, callback: FirebaseCallback<Boolean>) {
        try {
            playersRef.child(player.id).setValue(player)
                .addOnSuccessListener {
                    callback.onSuccess(true)
                }
                .addOnFailureListener { e ->
                    callback.onFailure(e.message ?: "Unknown error")
                }
        } catch (e: Exception) {
            callback.onFailure(e.message ?: "Unknown error")
        }
    }

    fun updateCommonData(common: Common, callback: FirebaseCallback<Boolean>) {
        try {
            commonRef.setValue(common)
                .addOnSuccessListener {
                    callback.onSuccess(true)
                }
                .addOnFailureListener { e ->
                    callback.onFailure(e.message ?: "Unknown error")
                }
        } catch (e: Exception) {
            callback.onFailure(e.message ?: "Unknown error")
        }
    }

    fun fetchCommonData(callback: FirebaseCallback<Common>) {
        try {
            commonRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val common = snapshot.getValue(Common::class.java)
                    if (common != null) {
                        callback.onSuccess(common)
                    } else {
                        callback.onFailure("Could not fetch common")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback.onFailure(error.message)
                }

            })
        } catch (e: Exception) {
            callback.onFailure(e.message ?: "Unknown error")
        }
    }

    fun addTransactionEntry(transaction: Transaction, callback: FirebaseCallback<Boolean>) {
        try {
            transactionsRef.child("${transaction.userID}-${transaction.dateTime}-${transaction.name}")
                .setValue(transaction)
                .addOnSuccessListener {
                    callback.onSuccess(true)
                }
                .addOnFailureListener { e ->
                    callback.onFailure(e.message ?: "Unknown Error")
                }
        } catch (e: Exception) {
            callback.onFailure(e.message ?: "Unknown error")
        }
    }

    fun fetchAllTransactions(callback: FirebaseCallback<List<Transaction>>) {
        try {
            transactionsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val transactions = mutableListOf<Transaction>()
                    for (userSnapshot in snapshot.children) {
                        val transaction = userSnapshot.getValue(Transaction::class.java)
                        transaction?.let { transactions.add(transaction) }
                    }
                    callback.onSuccess(transactions)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback.onFailure(error.message)
                }
            })
        } catch (e: Exception) {
            callback.onFailure(e.message ?: "Unknown error")
        }
    }

    fun updatePlayer(player: Player, callback: FirebaseCallback<Boolean>) {
        try {
            playersRef.child(player.id).setValue(player)
                .addOnSuccessListener {
                    callback.onSuccess(true)
                }
                .addOnFailureListener { e ->
                    callback.onFailure(e.message ?: "Unknown error")
                }
        } catch (e: Exception) {
            callback.onFailure(e.message ?: "Unknown error")
        }
    }

    fun updateTransactionEntry(transaction: Transaction, callback: FirebaseCallback<Boolean>) {
        try {
            transactionsRef.child("${transaction.userID}-${transaction.dateTime}-${transaction.name}").setValue(transaction)
                .addOnSuccessListener {
                    callback.onSuccess(true)
                }
                .addOnFailureListener { e ->
                    callback.onFailure(e.message ?: "Unknown error")
                }
        } catch (e: Exception) {
            callback.onFailure(e.message ?: "Unknown error")
        }
    }

    fun deletePlayer(userId: String, callback: FirebaseCallback<Boolean>) {
        try {
            playersRef.child(userId).removeValue()
                .addOnSuccessListener {
                    callback.onSuccess(true)
                }
                .addOnFailureListener { e ->
                    callback.onFailure(e.message ?: "Unknown error")
                }
        } catch (e: Exception) {
            callback.onFailure(e.message ?: "Unknown error")
        }
    }

    fun blockPlayer(userId: String, callback: FirebaseCallback<Boolean>) {
        try {
            val blockedUserRef = playersRef.child(userId).child("blocked")
            blockedUserRef.setValue(true)
                .addOnSuccessListener {
                    callback.onSuccess(true)
                }
                .addOnFailureListener { e ->
                    callback.onFailure(e.message ?: "Unknown error")
                }
        } catch (e: Exception) {
            callback.onFailure(e.message ?: "Unknown error")
        }
    }
}
