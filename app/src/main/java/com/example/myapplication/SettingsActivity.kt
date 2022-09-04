package com.example.myapplication

import android.R
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.example.myapplication.BaseDeDonnées.StatsRepository
import com.example.myapplication.databinding.HeaderLayoutBinding
import com.example.myapplication.databinding.SettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.ArrayList

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: SettingsBinding
    private lateinit var toggle : ActionBarDrawerToggle
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseRef : DatabaseReference
    private lateinit var headerLayout : HeaderLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(binding.root)

        binding.buttonResetPassword.setOnClickListener {
            resetPassword()
        }

        val languages = arrayListOf<String>("Français", "English")

        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.simple_spinner_item, languages)

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLanguages.setAdapter(arrayAdapter)
        binding.spinnerLanguages.setSelection(0)


        firebaseAuth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().getReference("players")

        // remplir les éléments en haut du toggle

        val inflater: LayoutInflater = this@SettingsActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val viewGroup : ViewGroup = findViewById (com.example.myapplication.R.id.nav_view)
        val view = inflater.inflate(com.example.myapplication.R.layout.header_layout, viewGroup)
        val name : TextView = view.findViewById(com.example.myapplication.R.id.text_username)
        val email : TextView = view.findViewById(com.example.myapplication.R.id.text_user_mail)
        val image : ImageView = view.findViewById(com.example.myapplication.R.id.image_user)
        headerLayout = HeaderLayoutBinding.inflate(layoutInflater)

        databaseRef.child(firebaseAuth.currentUser!!.uid).child("name").get().addOnSuccessListener {
            name.text = it.value.toString()
        }
        databaseRef.child(firebaseAuth.currentUser!!.uid).child("email").get().addOnSuccessListener {
            email.text = it.value.toString()
        }
        databaseRef.child(firebaseAuth.currentUser!!.uid).child("imageAvatarUrl").get().addOnSuccessListener {
            Glide.with(headerLayout.root).load(Uri.parse(it.value.toString())).into(image)
        }

        // gestion du toggle

        toggle = ActionBarDrawerToggle(this,binding.drawerLayout, com.example.myapplication.R.string.open,
            com.example.myapplication.R.string.close)
        binding.drawerLayout.addDrawerListener(toggle) // add le toggle au layout
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navView.setNavigationItemSelectedListener {
            when(it.itemId){
                com.example.myapplication.R.id.item_profil -> startActivity(Intent(this,ProfilActivity::class.java))
                com.example.myapplication.R.id.item_leaderboard -> StatsRepository().updateDate { startActivity(Intent(this,LeaderBoardActivity::class.java)) }
                com.example.myapplication.R.id.item_home -> StatsRepository().updateDate {  startActivity(Intent(this,MainActivity::class.java)) }
                com.example.myapplication.R.id.item_logout -> MainActivity().dialog()
                //R.id.item_settings ->
                com.example.myapplication.R.id.item_rate -> startActivity(Intent(this,WaitingActivity::class.java))
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

    override fun onStart() {
        super.onStart()
    }

    fun resetPassword() {
        startActivity(Intent(this, ForgotPasswordActivity::class.java))
    }
}