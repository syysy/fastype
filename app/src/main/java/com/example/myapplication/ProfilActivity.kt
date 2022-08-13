package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View.inflate
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myapplication.BaseDeDonnées.StatsRepository
import com.example.myapplication.databinding.ForgotyourpasswordBinding.bind
import com.example.myapplication.databinding.ForgotyourpasswordBinding.inflate
import com.example.myapplication.databinding.GameBinding
import com.example.myapplication.databinding.ProfilBinding
import com.example.myapplication.objets.ProfilModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.FileReader
import java.io.InputStreamReader
import java.util.*

class ProfilActivity : AppCompatActivity(){

    private lateinit var binding: ProfilBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        binding = ProfilBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Changement de l'image du profil

        firebaseAuth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().getReference("players")

        var userDate = firebaseAuth.currentUser
        var date = Date(userDate!!.metadata!!.creationTimestamp)

        databaseRef.addValueEventListener( object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(p0: DataSnapshot) {
                // recolter la liste
                for(i in p0.children){
                    val user = i.getValue(ProfilModel::class.java)
                    if (user != null && user.email == firebaseAuth.currentUser!!.email){
                        Glide.with(binding.root).load(Uri.parse(user.imageAvatarUrl)).into(binding.imageProfil)
                        binding.textRank.text = "Rank : " + (StatsRepository.Singleton.listPlayer.indexOf(user) + 1).toString()
                        binding.textMoyenne.text = "Mean : " + user.moyenne
                        binding.textPseudo.text = user.name
                        binding.textNbGameJouees.text = "Game Played : " + user.numberGamePlayed.toString()
                        binding.textCompteCreationDate.text = "Account Created : \n$date"
                        /*val reader =assets.open("country.json").bufferedReader().use { it.readText() }
                        val countryJson = Gson().fromJson(reader, JsonObject::class.java)
                        println(countryJson[user.country].toString() + "\n" + "\n" + "\n" + "\n" + "\n")
                        Glide.with(binding.root).load(Uri.parse(countryJson[user.country].toString())).into(binding.imageCountry)*/
                    }
                }
            }
            override fun onCancelled(p0: DatabaseError) {}
        })
    }



}