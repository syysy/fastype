package com.example.myapplication

import android.R
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.myapplication.databinding.SettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: SettingsBinding
    private lateinit var toggle: ActionBarDrawerToggle

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

        val languages = arrayListOf<String>("Français", "English")

        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.simple_spinner_item, languages)

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLanguages.setAdapter(arrayAdapter)
        binding.spinnerLanguages.setSelection(0)
    }

    override fun onStart() {
        super.onStart()
    }

    fun resetPassword() {
        startActivity(Intent(this, ForgotPasswordActivity::class.java))
    }
}