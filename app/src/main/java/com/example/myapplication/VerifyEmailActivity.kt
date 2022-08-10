package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ForgotyourpasswordBinding
import com.example.myapplication.databinding.LoginBinding
import com.example.myapplication.databinding.VerifyemailBinding
import com.google.firebase.auth.FirebaseAuth

class VerifyEmailActivity : AppCompatActivity() {

    private lateinit var binding: VerifyemailBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = VerifyemailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.buttonRetry.setOnClickListener {
            if (firebaseAuth.currentUser!!.isEmailVerified ){
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
            }else{
                Toast.makeText(this,"Email not verified !", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonSendEmail.setOnClickListener {
            firebaseAuth.currentUser!!.sendEmailVerification().addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(this,"Email resend !", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this,"Email could'nt be send retry later !", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.buttonBackToLogin.setOnClickListener {
            val intent = Intent(this,LoginBinding::class.java)
            startActivity(intent)
        }
    }


}