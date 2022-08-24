package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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


class MainActivity : AppCompatActivity() {

    private lateinit var toggle : ActionBarDrawerToggle
    private lateinit var binding: GameBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseRef : DatabaseReference
    private lateinit var headerLayout : HeaderLayoutBinding
    private val sauvegarde = mutableListOf<String>()
    private lateinit var userModel : ProfilModel
    private var scoreOfGame = 0

    private val Timer = object : CountDownTimer(60000, 1000) {
        var run = false

        @SuppressLint("SetTextI18n")
        override fun onTick(millisUntilFinished: Long) {
            val seconds = millisUntilFinished / 1000
            binding.timer.text = "${seconds / 60}:${if (seconds < 10) "0$seconds" else seconds}"
        }

        @SuppressLint("SetTextI18n")
        override fun onFinish() {
            this.run = false
            userModel.newGame(scoreOfGame)
            databaseRef.child(userModel.email).child("bestGame").setValue(userModel.bestGame)
            databaseRef.child(userModel.email).child("moyenne").setValue(userModel.moyenne)
            databaseRef.child(userModel.email).child("nbJeu").setValue(userModel.numberGamePlayed)
            binding.timer.text = "01:00"
            val oldRank = StatsRepository.Singleton.listPlayer.indexOf(userModel) + 1
            StatsRepository().updateDate { popupEndGame(oldRank) }
            stopGame()
        }
    }

    private val Jeu = object : TextWatcher {

        @SuppressLint("UseCompatLoadingForDrawables")
        override fun afterTextChanged(s: Editable?) {
            if (!Timer.run) { // commence une nouvelle partie
                Timer.start()
                Timer.run = true
                scoreOfGame = 0
            }

            val text = s.toString().strip()
            if (text == sauvegarde[0]) {
                scoreOfGame++ // aujout d'un point dans le score
                sauvegarde.removeAt(0) // suppression du premier élément de la liste, qui viens d'être trouvé
                binding.textInputGame.setText("") // reset du champ de texte
                updateListWords() // mise à jour de la liste des mots non trouvés
            } else if (text == "") {
                binding.textGame.background = getDrawable(R.drawable.back) // si le champ de texte est vide, on change la couleur de fond en blanc
            } else if (sauvegarde[0].startsWith(text)) {
                binding.textGame.background = getDrawable(R.drawable.backgreen) // si le mot commence par le mot tapé, on change la couleur de fond en vert
            } else {
                binding.textGame.background = getDrawable(R.drawable.backred) // sinon on change la couleur de fond en rouge car il y a une erreur
            }
        }


        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {


        }


        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        }
    }


    @SuppressLint("SetTextI18n")
    fun stopGame() {
        this.Timer.cancel()
        this.scoreOfGame = 0
        this.binding.timer.text = "01:00"
        this.scannerEtAjout()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = GameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.scannerEtAjout()

        binding.buttonRefresh.setOnClickListener {
            this.stopGame()
        }


        binding.imageProfil.setOnClickListener {
            val intent = Intent(this,ProfilActivity::class.java)
            startActivity(intent)
        }

        // header changements
        val inflater : LayoutInflater = this@MainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
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
                for (i in p0.children){
                    val user = i.getValue(ProfilModel::class.java) // transfo de chaque user en objet Profil Model
                    if ((user != null) && (user.email == firebaseAuth.currentUser!!.email)) {
                        userModel = user
                        // header layout
                        Glide.with(headerLayout.root).load(Uri.parse(user.imageAvatarUrl)).into(image)
                        email.text = firebaseAuth.currentUser!!.email
                        name.text = user.name
                        // Profil
                        Glide.with(binding.root).load(Uri.parse(user.imageAvatarUrl)).into(binding.imageProfil)
                        binding.textPlayerName.text = user.name
                        StatsRepository().updateDate { binding.textPlayerRank.text = "Rank : " + (StatsRepository.Singleton.listPlayer.indexOf(user) + 1) }
                        try {
                            val obj = JSONObject(loadJSONFromAsset())
                            if (user.country == "Unknown"){
                                Glide.with(binding.root).load(Uri.parse(obj[user.country].toString())).into(binding.imagePlayerCountry)
                            } else {
                                ProfilActivity.Utils().fetchSVG(this@MainActivity, obj[user.country].toString(),binding.imagePlayerCountry)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                        break
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
                R.id.item_home -> StatsRepository().updateDate {  startActivity(Intent(this,MainActivity::class.java)) }
                R.id.item_logout -> dialog()
                //R.id.item_settings ->
                R.id.item_rate -> startActivity(Intent(this,WaitingActivity::class.java))
                //R.id.item_share ->
            }
            true
        }

        // jeu

        this.binding.textInputGame.addTextChangedListener(this.Jeu)


    }


    // Dialog box disconnect
    fun dialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("Do you want to disconnect ?")
            .setCancelable(false)
            .setPositiveButton("Yes", DialogInterface.OnClickListener{ _, _ -> disconnect()})
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() })
        val alert = dialogBuilder.create()
        alert.setTitle("Disconnect ?")
        alert.show()
    }


    fun popupEndGame(oldRank: Int) {
        val popupBuilder = AlertDialog.Builder(this)
        val actualRank = StatsRepository.Singleton.listPlayer.indexOf(userModel) + 1
        popupBuilder.setMessage("recap:" + "score: ${this.scoreOfGame}}\n" + "      " + "rank: $actualRank" + if (oldRank > actualRank) "▲ ${oldRank - actualRank}" else "")
            .setCancelable(false)
            .setPositiveButton("ok", DialogInterface.OnClickListener{ popup, _ -> popup.cancel() })
            .setNegativeButton("see leaderboard", DialogInterface.OnClickListener { _, _ -> StatsRepository().updateDate { startActivity(Intent(this, LeaderBoardActivity::class.java)) } })
        val alert = popupBuilder.create()
        alert.setTitle("Game finished !")
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

    fun updateListWords() {
        var displayData = ""
        for (word in sauvegarde) {
            displayData += "$word "
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

    override fun onStart() {
        super.onStart()
        StatsRepository().updateDate {

        }
    }
}



