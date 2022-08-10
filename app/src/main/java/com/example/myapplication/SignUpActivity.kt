package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.databinding.LoginBinding
import com.example.myapplication.databinding.RegisterBinding
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: RegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = RegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.buttonCreateAccount.setOnClickListener {

            val email = binding.textInputMail.text.toString()
            val password = binding.textInputPassword.text.toString()
            val confirmPassword = binding.textInputPasswordConfirm.text.toString()
            val name = binding.textInputPseudo.text.toString()

            // requête au serveur

            val params = mutableMapOf<Any?,Any?>()
            params["email"] = email
            params["name"] = name
            val jsonObject = JSONObject(params)

            val queue = Volley.newRequestQueue(this)
            val url = "https://fastype.mathieuazerty.repl.co/new_player"
            val jsonRequest = JsonObjectRequest(Request.Method.POST,url,jsonObject, {
                    response ->
                // Process the json
                try {
                    Toast.makeText(this,response.toString(),Toast.LENGTH_SHORT).show()
                }catch (e:Exception){
                    Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show()
                }

            }, {
                // Error in request
                Toast.makeText(this,it.toString(),Toast.LENGTH_SHORT).show()
            })


            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()){
                if ( password == confirmPassword ){
                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                        if (it.isSuccessful){
                            queue.add(jsonRequest) // Envoie de la requête https
                            firebaseAuth.currentUser!!.sendEmailVerification()
                            val intent = Intent(this,SignInActivity::class.java)
                            startActivity(intent)
                        }else{
                            println("test")
                            Toast.makeText(this,it.exception.toString(),Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    Toast.makeText(this, "$password, $confirmPassword",Toast.LENGTH_SHORT).show()
                    // Toast.makeText(this,"Password is not matching !",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"Empty fields are not allowed",Toast.LENGTH_SHORT).show()
            }
        }
    }
}