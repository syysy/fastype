package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewbinding.ViewBinding
import com.example.myapplication.BaseDeDonnées.StatsRepository
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class NavBar(private val context: Context): AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val version = "1.0.0"



    fun navItems(navBar: NavigationView) {

        if (context is MainActivity) {
            navBar.menu.findItem(R.id.item_home).isChecked = true
            navBar.menu.findItem(R.id.item_home).isEnabled = false
        }
        if (context is ProfilActivity) {
            navBar.menu.findItem(R.id.item_profil).isChecked = true
            navBar.menu.findItem(R.id.item_profil).isEnabled = false
        }
        if (context is LeaderBoardActivity) {
            navBar.menu.findItem(R.id.item_leaderboard).isChecked = true
            navBar.menu.findItem(R.id.item_leaderboard).isEnabled = false
        }
        if (context is SettingsActivity) {
            navBar.menu.findItem(R.id.item_settings).isChecked = true
            navBar.menu.findItem(R.id.item_settings).isEnabled = false
        }


        navBar.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.item_profil -> { context.startActivity(Intent(this.context, ProfilActivity::class.java))
                    finish()}
                R.id.item_leaderboard ->  { StatsRepository().updateDate { context.startActivity(Intent(this.context, LeaderBoardActivity::class.java)) }
                    finish()}
                R.id.item_home -> { StatsRepository().updateDate { context.startActivity(Intent(this.context, MainActivity::class.java)) }
                    finish()}
                R.id.item_logout -> dialog()
                R.id.item_settings -> {context.startActivity(Intent(this.context, SettingsActivity::class.java))
                finish()}
                R.id.item_sendEmail -> sendEmailToggle()
                //R.id.item_share ->
            }
            true
        }
    }


    // Dialog box disconnect
    private fun dialog() {
        val dialogBuilder = AlertDialog.Builder(this.context)
        dialogBuilder.setMessage("Do you want to disconnect ?")
            .setCancelable(false)
            .setPositiveButton("Yes", DialogInterface.OnClickListener{ _, _ -> disconnect()})
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() })
        val alert = dialogBuilder.create()
        alert.setTitle("Disconnect ?")
        alert.show()
    }

    private fun disconnect() {
        firebaseAuth.signOut()
        context.startActivity(Intent(this.context, SignInActivity::class.java))
        finish()
    }

    private fun sendEmailToggle(){
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "plain/text"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("fastype.app@gmail.com"))
        context.startActivity(Intent.createChooser(intent, ""))
        finish()
    }
}