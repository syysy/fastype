package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.HeaderLayoutBinding
import com.example.myapplication.databinding.SettingsBinding
import com.example.myapplication.objets.ProfilModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONObject
import java.util.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: SettingsBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var headerLayout : HeaderLayoutBinding
    private lateinit var databaseRef : DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var userModel : ProfilModel

    override fun onBackPressed() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(binding.root)

        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle) // add le toggle au layout
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        NavBar(this).navItems(binding.navView)

        binding.buttonResetPassword.setOnClickListener {
            resetPassword()
        }

        firebaseAuth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().getReference("players")

        val inflater: LayoutInflater = this@SettingsActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val viewGroup : ViewGroup = findViewById(R.id.nav_view)
        val view = inflater.inflate(R.layout.header_layout, viewGroup)
        val name : TextView = view.findViewById(R.id.text_username)
        val email : TextView = view.findViewById(R.id.text_user_mail)
        val image : ImageView = view.findViewById(R.id.image_user)
        headerLayout = HeaderLayoutBinding.inflate(layoutInflater)

        userModel = ProfilModel().instancierProfil(firebaseAuth.currentUser!!.uid){
            name.text = userModel.name
            email.text = userModel.email
            Glide.with(headerLayout.root).load(userModel.imageAvatarUrl).into(image)
        }

        // Pubs

        val mAdViewBottom : AdView = binding.adViewBotSettings
        val adRequestBottom: AdRequest = AdRequest.Builder().build()
        mAdViewBottom.loadAd(adRequestBottom)

        val mAdViewTop : AdView = binding.adViewTopSettings
        val adRequestTop: AdRequest = AdRequest.Builder().build()
        mAdViewTop.loadAd(adRequestTop)


        val languages = arrayListOf<String>("Select a language", "Fran??ais", "English")

        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, languages)

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLanguages.setAdapter(arrayAdapter)


        var context : Context
        binding.spinnerLanguages.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position == 0) {
                    return
                }
                var language = parent.getItemAtPosition(position).toString()
                if (language == "Fran??ais") {
                    language = "fr"
                    context = LocaleHelper.setLocale(this@SettingsActivity, language)
                    resources.updateConfiguration(context.resources.configuration, context.resources.displayMetrics)
                    // condition si la langue change de celle par d??faut
                    if (LocaleHelper.SELECTED_LANGUAGE != "fr") {
                        val intent = Intent(this@SettingsActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else if (language == "English") {
                    language = "en"
                    context = LocaleHelper.setLocale(this@SettingsActivity, language)
                    resources.updateConfiguration(context.resources.configuration, context.resources.displayMetrics)
                    if (LocaleHelper.SELECTED_LANGUAGE != "en") {
                        val intent = Intent(this@SettingsActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }

                EditRessources(this@SettingsActivity).writeJsonFile("app_config.json", JSONObject().put("language", language))

            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
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
        finish()
    }
}