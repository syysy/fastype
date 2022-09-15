package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.myapplication.BaseDeDonnées.StatsRepository
import com.example.myapplication.databinding.LoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserInfo
import org.json.JSONObject

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: LoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onBackPressed() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        binding = LoginBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textCreateAccount.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.textForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
            finish()
            firebaseAuth.signOut()
        }

        binding.buttonLogin.setOnClickListener {
            val email = binding.textInputEmail.text.toString()
            val pass = binding.textInputPassword.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        if (firebaseAuth.currentUser!!.isEmailVerified) {
                            val language = "en"
                            val context = LocaleHelper.setLocale(this, language)
                            resources.updateConfiguration(context.resources.configuration, context.resources.displayMetrics)
                            EditRessources(this).writeJsonFile("app_config.json", JSONObject().put("language", language))
                            // lancement de la MainActivity + update du ladder
                            startActivity(Intent(this, WaitingActivity::class.java))
                            finish()
                        } else {
                            val intent = Intent(this, VerifyEmailActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (firebaseAuth.currentUser != null) {
            if (firebaseAuth.currentUser!!.isEmailVerified) {
                StatsRepository().updateDate { startActivity(Intent(this, MainActivity::class.java)) }
                finish()
            }
        } else {
            firebaseAuth.signOut()
        }
    }

}