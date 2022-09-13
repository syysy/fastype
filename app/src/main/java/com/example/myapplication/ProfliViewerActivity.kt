package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isInvisible
import com.bumptech.glide.Glide
import com.example.myapplication.BaseDeDonnées.StatsRepository
import com.example.myapplication.adapter.LeaderBoardAdapter
import com.example.myapplication.databinding.HeaderLayoutBinding
import com.example.myapplication.databinding.ProfilBinding
import com.example.myapplication.objets.ProfilModel
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserInfo
import com.google.firebase.database.*
import com.pixplicity.sharp.Sharp
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*


open class ProfliViewerActivity() : AppCompatActivity(){

    private lateinit var binding: ProfilBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseRef : DatabaseReference
    private lateinit var toggle : ActionBarDrawerToggle
    private lateinit var headerLayout : HeaderLayoutBinding
    private lateinit var userModel : ProfilModel

    override fun onBackPressed() {}

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProfilBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.imageClose.visibility = ImageView.VISIBLE

        binding.imageClose.setOnClickListener {
            startActivity(Intent(this, LeaderBoardActivity::class.java))
            finish()
        }

        // Changement de l'image du profil
        userModel = ProfilModel("", "", 0.0, 0, "")
        firebaseAuth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().getReference("players")

        val saveUser = intent.getStringExtra("user")

        val playerEmail = intent.getSerializableExtra("playerEmail") as String
        for (user in StatsRepository.Singleton.listPlayer) {
            if (user.email == playerEmail) {
                userModel = user
                break
            }
        }


        //var user = FirebaseAuth.getInstance().currentUser!!.email == playerEmail

        /*val date = Date(user!!.metadata!!.creationTimestamp)
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        val formattedDate = formatter.format(date)*/

        val imageProfil : ImageView = findViewById(R.id.imageProfil)
        val textRank : TextView = findViewById(R.id.text_rank)
        val textMoyenne : TextView = findViewById(R.id.textMoyenne)
        val textPseudo : TextView = findViewById(R.id.textPseudo)
        val textNbGameJouees : TextView = findViewById(R.id.textNbGameJouees)
        val textCompteCreationDate : TextView = findViewById(R.id.textCompteCreationDate)
        val textBestGame : TextView = findViewById(R.id.text_bestScore)
        val imageCountry : ImageView = findViewById(R.id.imageCountry)

        Glide.with(binding.root).load(Uri.parse(userModel.imageAvatarUrl)).into(imageProfil)
        textRank.text = "Rank : " + MainActivity().getRank()
        textMoyenne.text = "Mean : " + userModel.moyenne.toString()
        textPseudo.text = userModel.name
        textNbGameJouees.text = "Games played : " + userModel.numberGamePlayed.toString()
        //textCompteCreationDate.text = "Account creation date : $formattedDate"
        //textCompteCreationDate.isInvisible = true

        textBestGame.text = "Best game : " + userModel.bestGame.toString()

        val obj = JSONObject(LeaderBoardAdapter.OpenAsset().loadJsonFromRaw(this))
        if (userModel.country == "Unknown"){
            Glide.with(binding.root).load(Uri.parse(obj[userModel.country].toString())).into(imageCountry)
        } else {
            Utils().fetchSVG(binding.root.context, obj[userModel.country].toString(), imageCountry)
        }

        binding.imageBrush.isInvisible = true


        val mAdViewTop : AdView = binding.adViewTop
        val adRequestTop: AdRequest = AdRequest.Builder().build()
        mAdViewTop.loadAd(adRequestTop)

        MobileAds.initialize(this)
        val adLoader = AdLoader.Builder(this, "ca-app-pub-6513418938502245/2879336559")
            .forNativeAd { nativeAd ->
                val styles =
                    NativeTemplateStyle.Builder().build()
                val template = findViewById<TemplateView>(R.id.nativeAds)
                template.setStyles(styles)
                template.setNativeAd(nativeAd)
            }
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    class Utils {

        // on below line we are creating a variable for http client.
        private var httpClient: OkHttpClient? = null

        // on below line we are creating a function to load the svg from the url.
        // in below method we are specifying parameters as context,
        // url for the image and image view.
        fun fetchSVG(context: Context, url: String, target: ImageView) {
            // on below line we are checking
            // if http client is null
            if (httpClient == null) {
                // if it is null on below line
                // we are initializing our http client.
                httpClient =
                    OkHttpClient.Builder().cache(Cache(context.cacheDir, 5 * 1024 * 1014) as Cache)
                        .build() as OkHttpClient
            }

            // on below line we are creating a variable for our request and initialing it.
            var request: Request = Request.Builder().url(url).build()

            // on below line we are making a call to the http request on below lnie.
            httpClient!!.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                    // we are adding a default image if we gets any
                    // error while loading image from url.
                    target.setImageResource(R.drawable.avatar)
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call?, response: Response) {
                    // sharp is a library which will load stream which we generated
                    // from url in our target image view.
                    val stream: InputStream = response.body()!!.byteStream()
                    Sharp.loadInputStream(stream).into(target)
                    stream.close()
                }
            })
        }

    }

}