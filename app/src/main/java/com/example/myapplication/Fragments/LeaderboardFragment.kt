package com.example.myapplication.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.BaseDeDonnées.StatsRepository.Singleton.listPlayer
import com.example.myapplication.MainActivity
import com.example.myapplication.objets.ProfilModel
import com.example.myapplication.R
import com.example.myapplication.adapter.ItemDecoration
import com.example.myapplication.adapter.LeaderBoardAdapter

class LeaderboardFragment(
    private val context : MainActivity
): Fragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.leaderboard, container,false)
        
        val verticalRecyclerView = view.findViewById<RecyclerView>(R.id.vertical_recyclerView)
        verticalRecyclerView.adapter = LeaderBoardAdapter(context , listPlayer, R.layout.leaderboard_vertical_profiles)
        verticalRecyclerView.addItemDecoration(ItemDecoration())

        return view
    }

}