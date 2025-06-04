package com.example.animeheaven.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "anime_table")
data class AnimeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)
