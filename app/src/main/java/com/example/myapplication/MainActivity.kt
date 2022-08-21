package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myapplication.BaseDeDonnées.StatsRepository
import com.example.myapplication.databinding.GameBinding
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
                R.id.item1 -> startActivity(Intent(this,ProfilActivity::class.java))
                R.id.item2 -> StatsRepository().updateDate { startActivity(Intent(this,LeaderBoardActivity::class.java)) }
                R.id.item3 -> startActivity(Intent(this,MainActivity::class.java))
            }
            true
        }
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



