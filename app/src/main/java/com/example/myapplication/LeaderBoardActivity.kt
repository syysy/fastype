package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.BaseDeDonnées.StatsRepository
import com.example.myapplication.adapter.ItemDecoration
import com.example.myapplication.adapter.LeaderBoardAdapter
import com.example.myapplication.databinding.GameBinding
import com.example.myapplication.databinding.HeaderLayoutBinding
import com.example.myapplication.databinding.LeaderboardBinding
import com.example.myapplication.objets.ProfilModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import org.json.JSONException
import org.json.JSONObject

class LeaderBoardActivity :AppCompatActivity() {

    private lateinit var binding: LeaderboardBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseRef : DatabaseReference
    private lateinit var toggle : ActionBarDrawerToggle
    private lateinit var headerLayout : HeaderLayoutBinding
    private lateinit var userModel : ProfilModel

    override fun onBackPressed() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LeaderboardBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(binding.root)

        toggle = ActionBarDrawerToggle(this,binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle) // add le toggle au layout
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        NavBar(this).navItems(binding.navView)

        val verticalRecyclerView =
           this.findViewById<RecyclerView>(R.id.vertical_recyclerView)
        verticalRecyclerView.adapter = LeaderBoardAdapter(this,
            StatsRepository.Singleton.listPlayer,R.layout.leaderboard_vertical_profiles
        )
        verticalRecyclerView.addItemDecoration(ItemDecoration())

        // header changements
        val inflater: LayoutInflater = this@LeaderBoardActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val viewGroup : ViewGroup = findViewById (R.id.nav_view)
        val view = inflater.inflate(R.layout.header_layout, viewGroup)
        val name : TextView = view.findViewById(R.id.text_username)
        val email : TextView = view.findViewById(R.id.text_user_mail)
        val image : ImageView = view.findViewById(R.id.image_user)
        headerLayout = HeaderLayoutBinding.inflate(layoutInflater)

        firebaseAuth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().getReference("players")
        databaseRef.child(firebaseAuth.currentUser!!.uid).child("name").get().addOnSuccessListener {
            name.text = it.value.toString()
        }
        databaseRef.child(firebaseAuth.currentUser!!.uid).child("email").get().addOnSuccessListener {
            email.text = it.value.toString()
        }
        databaseRef.child(firebaseAuth.currentUser!!.uid).child("imageAvatarUrl").get().addOnSuccessListener {
            Glide.with(headerLayout.root).load(it.value.toString()).into(image)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }


}