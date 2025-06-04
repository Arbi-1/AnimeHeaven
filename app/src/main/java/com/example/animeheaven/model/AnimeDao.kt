package com.example.animeheaven.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AnimeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(animeList: List<AnimeEntity>)

    @Query("SELECT * FROM anime_table")
    suspend fun getAllAnime(): List<AnimeEntity>

    @Query("DELETE FROM anime_table")
    suspend fun clearAll()
}
