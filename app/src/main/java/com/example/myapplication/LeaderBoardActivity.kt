package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.BaseDeDonnées.StatsRepository
import com.example.myapplication.adapter.ItemDecoration
import com.example.myapplication.adapter.LeaderBoardAdapter
import com.example.myapplication.databinding.GameBinding
import com.example.myapplication.databinding.LeaderboardBinding
import com.google.firebase.auth.FirebaseAuth

class LeaderBoardActivity :AppCompatActivity() {

    private lateinit var binding: LeaderboardBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        binding = LeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val verticalRecyclerView = binding.root.findViewById<RecyclerView>(R.id.vertical_recyclerView)
        verticalRecyclerView.adapter = LeaderBoardAdapter(this , StatsRepository.Singleton.listPlayer, R.layout.leaderboard_vertical_profiles)
        verticalRecyclerView.addItemDecoration(ItemDecoration())

    }


}