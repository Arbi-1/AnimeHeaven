package com.example.animeheaven

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

object UserUtils {

    fun saveUserToFirestore(firebaseUser: FirebaseUser) {
        val user = User(
            uid = firebaseUser.uid,
            email = firebaseUser.email ?: "",
            displayName = firebaseUser.displayName ?: "",
            profileImageUrl = firebaseUser.photoUrl?.toString() ?: "",
            isAnonymous = firebaseUser.isAnonymous
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(user.uid)
            .set(user)
            .addOnSuccessListener {
                // You can log or toast here if needed
            }
            .addOnFailureListener { e ->
                // Handle error (optional)
            }
    }
}
