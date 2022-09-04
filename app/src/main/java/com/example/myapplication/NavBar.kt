package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
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

class NavBar(private val context: AppCompatActivity): AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val version = "1.0.0"
    private val navBar = context.findViewById<NavigationView>(R.id.nav_view)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.navBar.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.item_profil -> startActivity(Intent(this.context, ProfilActivity::class.java))
                R.id.item_leaderboard -> StatsRepository().updateDate { startActivity(Intent(this.context, LeaderBoardActivity::class.java)) }
                R.id.item_home -> StatsRepository().updateDate {  startActivity(Intent(this.context, MainActivity::class.java)) }
                R.id.item_logout -> dialog()
                R.id.item_settings -> startActivity(Intent(this.context, SettingsActivity::class.java))
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
        startActivity(Intent(this.context, SignInActivity::class.java))
    }

    private fun sendEmailToggle(){
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "plain/text"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("fastype.app@gmail.com"))
        startActivity(Intent.createChooser(intent, ""))
    }
}