package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ForgotyourpasswordBinding
import com.example.myapplication.databinding.LoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ForgotyourpasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ForgotyourpasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()


        binding.textBackToLogin.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        binding.buttonSubmit.setOnClickListener {
            val email = binding.textInputEmail.text.toString()
            if (email.isNotEmpty()) { // + vérif que l'email est présente dans la base de données
                firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener {
                    if (it.isSuccessful){
                        val intent = Intent(this, SignInActivity::class.java)
                        startActivity(intent)
                    }
                    Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}