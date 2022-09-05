package com.example.myapplication

import android.R
import android.app.LocaleManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.SettingsBinding
import org.json.JSONException
import java.util.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: SettingsBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private val languages = arrayListOf<String>("Français", "English")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(binding.root)

        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, com.example.myapplication.R.string.open, com.example.myapplication.R.string.close)
        binding.drawerLayout.addDrawerListener(toggle) // add le toggle au layout
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        NavBar(this).navItems(binding.navView)

        binding.buttonResetPassword.setOnClickListener {
            resetPassword()
        }


        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.simple_spinner_item, languages)

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLanguages.setAdapter(arrayAdapter)
        binding.spinnerLanguages.setSelection(0)

        binding.spinnerLanguages.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val language = parent.getItemAtPosition(position).toString()
                if (language == "Français") {
                    TODO("francais")
                } else {
                    TODO("anglais")
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    fun resetPassword() {
        startActivity(Intent(this, ForgotPasswordActivity::class.java))
    }

}