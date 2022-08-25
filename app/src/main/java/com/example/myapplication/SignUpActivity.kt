package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.RegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONObject

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: RegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = RegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.buttonCreateAccount.setOnClickListener {

            val email = binding.textInputMail.text.toString()
            val password = binding.textInputPassword.text.toString()
            val confirmPassword = binding.textInputPasswordConfirm.text.toString()
            val name = binding.textInputPseudo.text.toString()


            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if ( password == confirmPassword ) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            // requête au serveur
                            databaseRef = FirebaseDatabase.getInstance().getReference("players")
                            // creation des données du joueur dans la base de données firebase
                            databaseRef.child(firebaseAuth.currentUser!!.uid).child("bestGame").setValue(0)
                            databaseRef.child(firebaseAuth.currentUser!!.uid).child("country").setValue("Unknown")
                            databaseRef.child(firebaseAuth.currentUser!!.uid).child("email").setValue(email)
                            databaseRef.child(firebaseAuth.currentUser!!.uid).child("imageAvatarUrl").setValue("https://cdn.pixabay.com/photo/2013/07/13/10/44/man-157699_960_720.png")
                            databaseRef.child(firebaseAuth.currentUser!!.uid).child("moyenne").setValue(0)
                            databaseRef.child(firebaseAuth.currentUser!!.uid).child("name").setValue(name)
                            databaseRef.child(firebaseAuth.currentUser!!.uid).child("numberGamePlayed").setValue(0)

                            firebaseAuth.currentUser!!.sendEmailVerification()
                            val intent = Intent(this, SignInActivity::class.java)
                            startActivity(intent)
                        }else {
                            Toast.makeText(this, it.exception.toString(),Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this,"Password is not matching !",Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this,"Empty fields are not allowed",Toast.LENGTH_SHORT).show()
            }
        }
    }
}

