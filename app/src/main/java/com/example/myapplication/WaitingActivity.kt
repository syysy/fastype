package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.myapplication.BaseDeDonnées.StatsRepository
import com.example.myapplication.databinding.GameBinding
import com.example.myapplication.databinding.WaitingScreenBinding
import com.google.firebase.auth.FirebaseAuth

class WaitingActivity : AppCompatActivity() {

    private lateinit var binding : WaitingScreenBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = WaitingScreenBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser != null){
            StatsRepository().updateDate { startActivity(Intent(this,MainActivity::class.java)) }
        }else {
            startActivity(Intent(this,SignInActivity::class.java))
        }
    }
}