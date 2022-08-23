package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
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
    private lateinit var toggle : ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val verticalRecyclerView = binding.root.findViewById<RecyclerView>(R.id.vertical_recyclerView)
        verticalRecyclerView.adapter = LeaderBoardAdapter(this , StatsRepository.Singleton.listPlayer, R.layout.leaderboard_vertical_profiles)
        verticalRecyclerView.addItemDecoration(ItemDecoration())

        toggle = ActionBarDrawerToggle(this,binding.drawerLayout,R.string.open,R.string.close)
        binding.drawerLayout.addDrawerListener(toggle) // add le toggle au layout
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.item_profil -> startActivity(Intent(this,ProfilActivity::class.java))
                R.id.item_leaderboard -> StatsRepository().updateDate { startActivity(Intent(this,LeaderBoardActivity::class.java)) }
                R.id.item_home -> startActivity(Intent(this,MainActivity::class.java))
                //R.id.item_logout ->
                //R.id.item_rate ->
                //R.id.item_settings ->
                //R.id.item_share ->
            }
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}