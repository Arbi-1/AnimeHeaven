package com.example.animeheaven

data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val profileImageUrl: String = "",
    val isAnonymous: Boolean = false
)
