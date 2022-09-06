package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.myapplication.databinding.ForgotyourpasswordBinding
import com.example.myapplication.databinding.LoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ForgotyourpasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onBackPressed() {}

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        binding = ForgotyourpasswordBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        if (firebaseAuth.currentUser != null) {
            binding.textInputEmail.setText(firebaseAuth.currentUser?.email)
            binding.textBackToLogin.text = "Back to settings"
            binding.textBackToLogin.setOnClickListener {
                startActivity(Intent(this, SettingsActivity::class.java))
                finish()
            }
        } else {
            binding.textBackToLogin.setOnClickListener {
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        binding.buttonSubmit.setOnClickListener {
            val email = binding.textInputEmail.text.toString()
            if (email.isNotEmpty()) { // + vérif que l'email est présente dans la base de données
                firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener {
                    if (it.isSuccessful){
                        val intent = Intent(this, SignInActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Empty Field Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}