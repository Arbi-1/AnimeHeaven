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

        // Log screen view event
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, "MainActivity")
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity")
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)

        animeRecyclerView = findViewById(R.id.animeRecyclerView)
        animeRecyclerView.layoutManager = LinearLayoutManager(this)

        adapter = AnimeAdapter(animeList)
        animeRecyclerView.adapter = adapter

        // Load data asynchronously
        lifecycleScope.launch {
            val list = withContext(Dispatchers.Default) {
                generateAnimeList()
            }
            animeList.clear()
            animeList.addAll(list)
            adapter.notifyDataSetChanged()
        }
    }

    // This returns a list of Anime objects with explicit title param
    private fun generateAnimeList(): List<Anime> {
        return listOf(
            Anime(title = "Naruto"),
            Anime(title = "One Piece"),
            Anime(title = "Attack on Titan"),
            Anime(title = "My Hero Academia"),
            Anime(title = "Demon Slayer")
        )
    }
}
