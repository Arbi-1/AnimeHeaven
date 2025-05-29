package com.example.animeheaven

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.animeheaven.adapter.AnimeAdapter
import com.example.animeheaven.model.Anime

class MainActivity : AppCompatActivity() {


    private lateinit var animeRecyclerView: RecyclerView
    private lateinit var adapter: AnimeAdapter
    private val animeList = mutableListOf<Anime>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        animeRecyclerView = findViewById(R.id.animeRecyclerView)
        animeRecyclerView.layoutManager = LinearLayoutManager(this)

        // Add dummy data
        animeList.add(Anime("Naruto"))
        animeList.add(Anime("One Piece"))
        animeList.add(Anime("Attack on Titan"))
        animeList.add(Anime("My Hero Academia"))
        animeList.add(Anime("Demon Slayer"))

        adapter = AnimeAdapter(animeList)
        animeRecyclerView.adapter = adapter




    }
}
