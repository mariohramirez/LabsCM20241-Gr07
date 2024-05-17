package com.example.jetsnack.model

data class SnackResult(
    val id: Long,
    val name: String,
    val imageUrl: String,
    val price: Long,
    val tagline: String,
    val tags: Set<String> = emptySet()
)
