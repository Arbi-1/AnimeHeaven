package com.example.animeheaven

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.animeheaven.adapter.AnimeAdapter
import com.example.animeheaven.model.Anime
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var animeRecyclerView: RecyclerView
    private lateinit var adapter: AnimeAdapter
    private val animeList = mutableListOf<Anime>()

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Analytics
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        // Log screen view
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, "MainActivity")
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity")
        })

        animeRecyclerView = findViewById(R.id.animeRecyclerView)
        animeRecyclerView.layoutManager = LinearLayoutManager(this)

        adapter = AnimeAdapter(animeList)
        animeRecyclerView.adapter = adapter

        // Load data asynchronously
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                populateAnimeList()
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun populateAnimeList() {
        animeList.apply {
            clear()
            add(Anime("Naruto"))
            add(Anime("One Piece"))
            add(Anime("Attack on Titan"))
            add(Anime("My Hero Academia"))
            add(Anime("Demon Slayer"))
        }
    }
}
