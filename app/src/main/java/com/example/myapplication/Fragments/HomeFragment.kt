package com.example.myapplication.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapter.ItemDecoration
import com.example.myapplication.adapter.LeaderBoardAdapter
import java.io.File
import java.util.*
import kotlin.random.Random

class HomeFragment: Fragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.leaderboard, container,false)

        val verticalRecyclerView = view.findViewById<RecyclerView>(R.id.vertical_recyclerView)
        verticalRecyclerView.adapter = LeaderBoardAdapter(R.layout.leaderboard_vertical_profiles)
        verticalRecyclerView.addItemDecoration(ItemDecoration())

        return view
    }

}
