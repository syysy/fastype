package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.example.myapplication.BaseDeDonnées.StatsRepository
import com.example.myapplication.databinding.GameBinding
import com.example.myapplication.databinding.LoginBinding
import com.example.myapplication.objets.ProfilModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserInfo
import com.google.firebase.database.*
import java.io.File
import java.io.FileReader
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.random.Random
import com.opencsv.CSVReader
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    private lateinit var binding: GameBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        binding = GameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        scannerEtAjout()

        binding.leaderboardButton.setOnClickListener {
            val repo = StatsRepository()
            repo.updateDate {
                val intent = Intent(this,LeaderBoardActivity::class.java)
                startActivity(intent)
            }
        }
        binding.buttonRefresh.setOnClickListener{
            scannerEtAjout()
            // + reset de timer / new game
        }

        binding.imageProfil.setOnClickListener {
            val intent = Intent(this,ProfilActivity::class.java)
            startActivity(intent)
        }

        // Changement de l'image du profil


        firebaseAuth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().getReference("players")
        databaseRef.addValueEventListener( object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                // recolter la liste
                for(i in p0.children){
                    val user = i.getValue(ProfilModel::class.java)
                    if (user != null && user.email == firebaseAuth.currentUser!!.email){
                        Glide.with(binding.root).load(Uri.parse(user.imageAvatarUrl)).into(binding.imageProfil)
                    }
                }
            }
            override fun onCancelled(p0: DatabaseError) {}
        })
    }
    val sauvegarde = mutableListOf<String>()

    @SuppressLint("SetTextI18n")
    fun scannerEtAjout(){
        sauvegarde.clear()
        val listeMots = mutableListOf<String>()
        val minput = InputStreamReader(assets.open("listWords.csv"))
        val reader = BufferedReader(minput)
        var line : String?
        var displayData = ""
        while (reader.readLine().also { line = it } != null){
            listeMots.add(line!!)
        }
        listeMots.shuffle()
        for (j in 0 until 200){
            displayData += listeMots[j] + " "
            sauvegarde.add(listeMots[j])
        }
        binding.textGame.text = displayData

    }
}



