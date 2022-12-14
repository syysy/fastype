package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.myapplication.BaseDeDonnĂ©es.StatsRepository
import com.example.myapplication.databinding.GameBinding
import com.example.myapplication.databinding.WaitingScreenBinding
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONException
import org.json.JSONObject

class WaitingActivity : AppCompatActivity() {

    private lateinit var binding : WaitingScreenBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onBackPressed() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = WaitingScreenBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(binding.root)

        val editRessources = EditRessources(this)

        var context : Context
        try {
            val deviceLanguage = editRessources.loadEditableJsonFile("app_config.json")["language"].toString()
            context = LocaleHelper.setLocale(this, deviceLanguage)
            resources.updateConfiguration(context.resources.configuration, context.resources.displayMetrics)
        } catch (e: JSONException) {
            editRessources.writeJsonFile("app_config.json", JSONObject().put("language", "en"))
            context = LocaleHelper.setLocale(this, "en")
            resources.updateConfiguration(context.resources.configuration, context.resources.displayMetrics)
        }

        firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser != null){
            if (firebaseAuth.currentUser!!.isEmailVerified){
                StatsRepository().updateDate { startActivity(Intent(this, MainActivity::class.java))
                    finish() }
            } else {
                startActivity(Intent(this, VerifyEmailActivity::class.java))
                finish()
            }
        } else {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }
}