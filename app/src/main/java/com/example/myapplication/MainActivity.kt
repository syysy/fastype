package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.example.myapplication.BaseDeDonnées.StatsRepository
import com.example.myapplication.databinding.GameBinding
import com.example.myapplication.databinding.HeaderLayoutBinding
import com.example.myapplication.objets.ProfilModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var toggle : ActionBarDrawerToggle
    private lateinit var binding: GameBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseRef : DatabaseReference
    private lateinit var headerLayout : HeaderLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        scannerEtAjout()

        binding.buttonRefresh.setOnClickListener{
            scannerEtAjout()
            // + reset de timer / new game
        }

        binding.imageProfil.setOnClickListener {
            val intent = Intent(this,ProfilActivity::class.java)
            startActivity(intent)
        }
        // header changements
        val inflater: LayoutInflater = this@MainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val viewGroup : ViewGroup = findViewById (R.id.nav_view)
        val view = inflater.inflate(R.layout.header_layout, viewGroup)
        val name : TextView = view.findViewById(R.id.text_username)
        val email : TextView = view.findViewById(R.id.text_user_mail)
        val image : ImageView = view.findViewById(R.id.image_user)
        headerLayout = HeaderLayoutBinding.inflate(layoutInflater)

        // Changement de l'image du profil

        firebaseAuth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().getReference("players")
        databaseRef.addValueEventListener( object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(p0: DataSnapshot) {
                // recolter la liste
                for(i in p0.children){
                    val user = i.getValue(ProfilModel::class.java)
                    if ((user != null) && (user.email == firebaseAuth.currentUser!!.email)){
                        // header layout
                        Glide.with(headerLayout.root).load(Uri.parse(user.imageAvatarUrl)).into(image)
                        email.text = firebaseAuth.currentUser!!.email
                        name.text = user.name

                        // Profil
                        Glide.with(binding.root).load(Uri.parse(user.imageAvatarUrl)).into(binding.imageProfil)
                        binding.textPlayerName.text = user.name
                        binding.textPlayerRank.text = "Rank : " + (StatsRepository.Singleton.listPlayer.indexOf(user) + 1).toString()
                        try {
                            val obj = JSONObject(loadJSONFromAsset())
                            if (user.country == "Unknown"){
                                Glide.with(binding.root).load(Uri.parse(obj[user.country].toString())).into(binding.imagePlayerCountry)
                            }else{
                                ProfilActivity.Utils().fetchSVG(this@MainActivity,obj[user.country].toString(),binding.imagePlayerCountry)
                            }
                        }catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            override fun onCancelled(p0: DatabaseError) {}
        })

        // Pubs

        val mAdViewBottom : AdView = binding.adViewBottom
        val adRequestBottom: AdRequest = AdRequest.Builder().build()
        mAdViewBottom.loadAd(adRequestBottom)

        val mAdViewTop : AdView = binding.adViewTop
        val adRequestTop: AdRequest = AdRequest.Builder().build()
        mAdViewTop.loadAd(adRequestTop)

        // toggle en haut à gauche

        toggle = ActionBarDrawerToggle(this,binding.drawerLayout,R.string.open,R.string.close)
        binding.drawerLayout.addDrawerListener(toggle) // add le toggle au layout
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



        binding.navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.item_profil -> startActivity(Intent(this,ProfilActivity::class.java))
                R.id.item_leaderboard -> StatsRepository().updateDate { startActivity(Intent(this,LeaderBoardActivity::class.java)) }
                R.id.item_home -> startActivity(Intent(this,MainActivity::class.java))
                R.id.item_logout -> dialog(this)
                R.id.item_settings -> startActivity(Intent(this,WaitingActivity::class.java))
                //R.id.item_rate ->
                //R.id.item_share ->
            }
            true
        }
    }

    // Dialog box disconnect
    fun dialog(context : Context){
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setMessage("Do you want to disconnect ?")
            .setCancelable(false)
            .setPositiveButton("Yes",DialogInterface.OnClickListener{ _, _ -> disconnect()})
            .setNegativeButton("Cancel",DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() })
        val alert = dialogBuilder.create()
        alert.setTitle("Disconnect ?")
        alert.show()
    }

    fun disconnect(){
        firebaseAuth.signOut()
        startActivity(Intent(this,SignInActivity::class.java))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    val sauvegarde = mutableListOf<String>()

    @SuppressLint("SetTextI18n")
    fun scannerEtAjout(){
        sauvegarde.clear()
        val listeMots = mutableListOf<String>()
        val minput = InputStreamReader(assets.open("listWords.csv"))
        val reader = BufferedReader(minput)
        var line : String?
        var displayData = ""
        while (reader.readLine().also { line = it } != null){
            listeMots.add(line!!)
        }
        listeMots.shuffle()
        for (j in 0 until 200){
            displayData += listeMots[j] + " "
            sauvegarde.add(listeMots[j])
        }
        binding.textGame.text = displayData
    }

    fun loadJSONFromAsset(): String {
        val json: String?
        try {
            val inputStream = assets.open("country.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            val charset: Charset = Charsets.UTF_8
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer, charset)
        }
        catch (ex: IOException) {
            ex.printStackTrace()
            return ""
        }
        return json
    }
}



