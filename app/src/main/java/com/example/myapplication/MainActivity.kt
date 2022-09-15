package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.example.myapplication.BaseDeDonnées.StatsRepository
import com.example.myapplication.adapter.LeaderBoardAdapter
import com.example.myapplication.databinding.GameBinding
import com.example.myapplication.databinding.HeaderLayoutBinding
import com.example.myapplication.objets.ProfilModel
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var toggle : ActionBarDrawerToggle
    private lateinit var binding: GameBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseRef : DatabaseReference
    private lateinit var headerLayout : HeaderLayoutBinding
    private val listWordsCsvGame = mutableListOf<String>()
    @RequiresApi(Build.VERSION_CODES.N)
    private var listStringWordsCsv: MutableList<String>? = null
    private lateinit var userModel : ProfilModel
    private var scoreOfGame = 0
    private var deviceLanguage: String? = null
    private val editRessources = EditRessources(this)
    private var mRewardedAd: RewardedAd? = null

    private val Timer = object : CountDownTimer(60000, 1000) {
        var run = false

        @SuppressLint("SetTextI18n")
        override fun onTick(millisUntilFinished: Long) {
            val seconds = millisUntilFinished / 1000
            binding.timer.text = "${seconds / 60}:${if (seconds < 10) "0$seconds" else seconds}"
        }

        @RequiresApi(Build.VERSION_CODES.N)
        @SuppressLint("SetTextI18n")
        override fun onFinish() {
            userModel.newGame(scoreOfGame,getMoyenne())
            databaseRef.child(firebaseAuth.currentUser!!.uid).child("bestGame").setValue(userModel.bestGame)
            databaseRef.child(firebaseAuth.currentUser!!.uid).child("moyenne").setValue(userModel.moyenne)
            databaseRef.child(firebaseAuth.currentUser!!.uid).child("numberGamePlayed").setValue(userModel.numberGamePlayed)
            val oldRank = getRank(firebaseAuth.currentUser!!.email.toString())
            StatsRepository().updateDate { popupEndGame(oldRank)
                stopGame()
            }
        }
    }

    private val Jeu = object : TextWatcher {

        @RequiresApi(Build.VERSION_CODES.N)
        @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
        override fun afterTextChanged(s: Editable?) {
            if (!Timer.run) { // commence une nouvelle partie
                Timer.start()
                Timer.run = true
                scoreOfGame = 0
            }

            val text = s.toString().strip()
            if (text == this@MainActivity.listWordsCsvGame[0]) {
                this@MainActivity.listStringWordsCsv!!.removeAt(0)
                scoreOfGame++ // aujout d'un point dans le score
                this@MainActivity.listWordsCsvGame.removeAt(0) // suppression du premier élément de la liste, qui viens d'être trouvé
                binding.textInputGame.setText("") // reset du champ de texte
                setWordsText() // mise à jour de la liste des mots non trouvés
                when(deviceLanguage) {
                    "fr" -> {
                        binding.textPlayerScore.text = "$scoreOfGame mots"
                    }
                    else -> {
                        binding.textPlayerScore.text = "$scoreOfGame words"
                    }
                }
                 // mise à jour du score
            } else if (text == "") {
                binding.textGame.background = getDrawable(R.drawable.back) // si le champ de texte est vide, on change la couleur de fond en blanc
            } else if (this@MainActivity.listWordsCsvGame[0].startsWith(text)) {
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


    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    fun stopGame() {
        this.binding.textInputGame.setText("")
        this.Timer.cancel()
        this.Timer.run = false
        this.scoreOfGame = 0
        this.binding.timer.text = "01:00"
        when(deviceLanguage) {
            "fr" -> {
                binding.textPlayerScore.text = "0 mots"
            }
            else -> {
                binding.textPlayerScore.text = "0 words"
            }
        }
        this.loadWordsCSV()
        this.setWordsText()
    }

    override fun onBackPressed() {
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = GameBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(binding.root)

        toggle = ActionBarDrawerToggle(this,binding.drawerLayout,R.string.open,R.string.close)
        binding.drawerLayout.addDrawerListener(toggle) // add le toggle au layout
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        NavBar(this).navItems(binding.navView)


        try {
            this.deviceLanguage = this.editRessources.loadEditableJsonFile("app_config.json")["language"].toString()
            LocaleHelper.setLocale(this, this.deviceLanguage!!)
        } catch (e: JSONException) {
            this.editRessources.writeJsonFile("app_config.json", JSONObject().put("language", "en"))
            this.deviceLanguage = "en"
            LocaleHelper.setLocale(this, "en")
        }


        this.loadWordsCSV()
        this.setWordsText()

        binding.buttonRefresh.setOnClickListener {
            this.stopGame()
        }
        binding.imageProfil.setOnClickListener {
            val intent = Intent(this,ProfilActivity::class.java)
            startActivity(intent)
            //finish()
        }


        // header changements
        val inflater : LayoutInflater = this@MainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val viewGroup : ViewGroup = findViewById (R.id.nav_view)
        val view = inflater.inflate(R.layout.header_layout, viewGroup)
        val name : TextView = view.findViewById(R.id.text_username)
        val email : TextView = view.findViewById(R.id.text_user_mail)
        val image : ImageView = view.findViewById(R.id.image_user)
        headerLayout = HeaderLayoutBinding.inflate(layoutInflater)

        val imageProfil : ImageView = findViewById(R.id.imageProfil)
        val textPlayerName : TextView = findViewById(R.id.text_player_name)
        val textPlayerRank : TextView = findViewById(R.id.text_player_rank)
        val imageCountry : ImageView = findViewById(R.id.image_player_country)

        firebaseAuth = FirebaseAuth.getInstance()
        // récup du currentUser

        userModel = ProfilModel().instancierProfil(firebaseAuth.currentUser!!.uid){
            textPlayerName.text = userModel.name
            name.text = userModel.name
            email.text = userModel.email
            try {
                val obj = JSONObject(LeaderBoardAdapter.OpenAsset().loadJsonFromRaw(this))
                if (userModel.country == "Unknown"){
                    Glide.with(applicationContext).load(Uri.parse(obj[userModel.country].toString())).into(imageCountry)
                } else {
                    ProfilActivity.Utils().fetchSVG(applicationContext, obj[userModel.country].toString(),imageCountry)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            Glide.with(applicationContext).load(Uri.parse(userModel.imageAvatarUrl)).into(image)
            Glide.with(applicationContext).load(Uri.parse(userModel.imageAvatarUrl)).into(imageProfil)

            when (this.deviceLanguage) {
                "fr" -> {
                    StatsRepository().updateDate {  binding.textPlayerRank.text = "Rang : " + getRank(userModel.uid) }
                }
                else -> {
                    StatsRepository().updateDate {  binding.textPlayerRank.text = "Rank : " + getRank(userModel.uid) }
                }
            }
        }




        println( "Token :" + {firebaseAuth.currentUser!!.getIdToken(true)})

        // afficher les players de la listPlayer du singleton statsrepository



        // Pubs
        val mAdViewBottom : AdView = binding.adViewBottom
        val adRequestBottom: AdRequest = AdRequest.Builder().build()
        mAdViewBottom.loadAd(adRequestBottom)

        val mAdViewTop : AdView = binding.adViewTop
        val adRequestTop: AdRequest = AdRequest.Builder().build()
        mAdViewTop.loadAd(adRequestTop)

        // jeu
        this.binding.textInputGame.addTextChangedListener(this.Jeu)



        // Reward Ads



        MobileAds.initialize(this) {}

        var adRequest = AdRequest.Builder().build()

        // faire un callback pour que la RewardActivity se lance seulement quand l'ad est chargée

        RewardedAd.load(this,"ca-app-pub-6513418938502245/6795073405", adRequest, object : RewardedAdLoadCallback() {

            override fun onAdFailedToLoad(adError: LoadAdError) {
                mRewardedAd = null
            }

            override fun onAdLoaded(rewardedAd: RewardedAd) {
                mRewardedAd = rewardedAd
                mRewardedAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
                    override fun onAdClicked() {
                        // Called when a click is recorded for an ad.
                    }

                    override fun onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        // Set the ad reference to null so you don't show the ad a second time.
                        mRewardedAd = null
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        // Called when ad fails to show.
                        mRewardedAd = null
                    }

                    override fun onAdImpression() {
                        // Called when an impression is recorded for an ad.
                    }

                    override fun onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                    }
                }
            }
        })

        binding.textHelpUs.setOnClickListener {
            if (mRewardedAd != null) {
                val activityContext: Activity = this
                mRewardedAd?.show(activityContext, object : OnUserEarnedRewardListener {
                    override fun onUserEarnedReward(rewardItem: RewardItem) {
                        // Handle the reward.
                        val rewardAmount: Int = rewardItem.amount
                        val rewardType: String = rewardItem.type
                        // ...
                    }
                })
            } else {
                Log.d("TAG", "The rewarded ad wasn't ready yet.")
            }
        }

    }


    @SuppressLint("SetTextI18n")
    fun popupEndGame(oldRank: Int) {
        val newRank = getRank(userModel.uid)
        val popupBuilder = Dialog(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        popupBuilder.setContentView(R.layout.custom_dialog_endgame)
        // all components of the popup
        val score = popupBuilder.findViewById<TextView>(R.id.popup_score)
        val bestScore = popupBuilder.findViewById<TextView>(R.id.popup_best_score)
        val mean  = popupBuilder.findViewById<TextView>(R.id.popup_mean)
        val rank  = popupBuilder.findViewById<TextView>(R.id.popup_rank)

        score.text = "Score : $scoreOfGame"
        bestScore.text = "Best score : ${userModel.bestGame}"
        mean.text = "Mean : ${userModel.moyenne}"
        if (oldRank > newRank) {
            val up : Int = oldRank - newRank
            rank.text = "Rank : $newRank (▲ $up)"
        } else {
            rank.text = "Rank : $oldRank"
        }

        val buttonRestart = popupBuilder.findViewById<Button>(R.id.popup_button_restart)
        val buttonLeaderBoard = popupBuilder.findViewById<Button>(R.id.popup_button_leaderboard)

        buttonRestart.setOnClickListener {
            popupBuilder.dismiss()
        }
        buttonLeaderBoard.setOnClickListener {
            popupBuilder.dismiss()
            startActivity(Intent(this,LeaderBoardActivity::class.java))
            finish()
        }

        popupBuilder.show()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    fun getRank(uid : String) : Int{
        val rank : Int
        val listPlayer = StatsRepository.Singleton.listPlayer
        for (i in 0 until listPlayer.size) {
            if (listPlayer[i].uid == uid) {
                if (i > 0 && listPlayer[i].bestGame == listPlayer[i - 1].bestGame){
                    rank = i
                } else {
                    rank = i + 1
                }
                return rank
            }
        }
        return -1
    }

    fun getMoyenne() : Double{
        firebaseAuth = FirebaseAuth.getInstance()
        val moyenne : Double
        val listPlayer = StatsRepository.Singleton.listPlayer
        for ((index, i) in listPlayer.withIndex()) {
            if (i.email == firebaseAuth.currentUser!!.email) {
                moyenne = i.moyenne
                return moyenne
            }
        }
        return -1.0
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun loadWordsCSV() {
        this.listWordsCsvGame.clear()

        // ouvre le fichier words.csv
        val reader = when(this.deviceLanguage) {
            "fr" -> editRessources.loadTextFile(R.raw.mots_fr)
            else -> editRessources.loadTextFile(R.raw.mots_en)
        }

        // créer une liste des 200 premiers mots tirés au hasard
        val listWordsString = mutableListOf<String>()
        var lines = reader.lines().toList()
        lines = lines.shuffled()
        lines = lines.take(200)

        var tmp = 0
        for (line in lines) {
            var line = line
            this.listWordsCsvGame.add(line)
            var length = line.length

            for (i in 0..tmp) {
                length += this.listWordsCsvGame[this.listWordsCsvGame.lastIndex].length
            }

            if (length > 26) {
                tmp = 0
                line += "\n"
            } else {
                tmp++
                line += " "
            }

            listWordsString.add(line)
        }

        this.listStringWordsCsv = listWordsString
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun setWordsText() {
        binding.textGame.text = this.listStringWordsCsv!!.joinToString("")
    }

    override fun onStart() {
        super.onStart()
        StatsRepository().updateDate {

        }
    }
}




