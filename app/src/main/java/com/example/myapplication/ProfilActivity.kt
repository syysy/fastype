package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View.inflate
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myapplication.BaseDeDonnées.StatsRepository
import com.example.myapplication.databinding.ForgotyourpasswordBinding.bind
import com.example.myapplication.databinding.ForgotyourpasswordBinding.inflate
import com.example.myapplication.databinding.GameBinding
import com.example.myapplication.databinding.HeaderLayoutBinding
import com.example.myapplication.databinding.ProfilBinding
import com.example.myapplication.objets.ProfilModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.pixplicity.sharp.Sharp
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.FileReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.util.*

open class ProfilActivity : AppCompatActivity(){

    private lateinit var binding: ProfilBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseRef : DatabaseReference
    private lateinit var toggle : ActionBarDrawerToggle
    private lateinit var headerLayout : HeaderLayoutBinding
    private lateinit var userModel : ProfilModel

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ProfilBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // header changements
        val inflater: LayoutInflater = this@ProfilActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val viewGroup : ViewGroup = findViewById (R.id.nav_view)
        val view = inflater.inflate(R.layout.header_layout, viewGroup)
        val name : TextView = view.findViewById(R.id.text_username)
        val email : TextView = view.findViewById(R.id.text_user_mail)
        val image : ImageView = view.findViewById(R.id.image_user)
        headerLayout = HeaderLayoutBinding.inflate(layoutInflater)

        // Changement de l'image du profil

        firebaseAuth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().getReference("players")

        val userDate = firebaseAuth.currentUser
        val date = Date(userDate!!.metadata!!.creationTimestamp)
        println(">"+StatsRepository.Singleton.listPlayer+"<")
        databaseRef.addValueEventListener( object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(p0: DataSnapshot) {
                // recolter la liste
                for(i in p0.children){
                    val user = i.getValue(ProfilModel::class.java)
                    if (user != null && user.email == firebaseAuth.currentUser!!.email){
                        userModel = user
                        break
                    }
                }
            }
            override fun onCancelled(p0: DatabaseError) {}
        })

        Glide.with(headerLayout.root).load(Uri.parse(userModel.imageAvatarUrl)).into(image)
        email.text = firebaseAuth.currentUser!!.email
        name.text = userModel.name

        val imageProfil : ImageView = findViewById(R.id.image_user)
        val textRank : TextView = findViewById(R.id.text_rank)
        val textMoyenne : TextView = findViewById(R.id.textMoyenne)
        val textPseudo : TextView = findViewById(R.id.text_pseudo)
        val textNbGameJouees : TextView = findViewById(R.id.textNbGameJouees)
        val textCompteCreationDate : TextView = findViewById(R.id.textCompteCreationDate)
        val imageCountry : ImageView = findViewById(R.id.imageCountry)

        Glide.with(binding.root).load(Uri.parse(userModel.imageAvatarUrl)).into(imageProfil)
        textRank.text = "Rank : " + (StatsRepository.Singleton.listPlayer.indexOf(userModel) + 1)
        textMoyenne.text = "Mean : " + userModel.moyenne
        textPseudo.text = userModel.name
        textNbGameJouees.text = "Game Played : " + userModel.numberGamePlayed.toString()
        textCompteCreationDate.text = "Account Created : \n$date"
        try {
            val obj = JSONObject(loadJSONFromAsset())
            if (userModel.country == "Unknown"){
                Glide.with(binding.root).load(Uri.parse(obj[userModel.country].toString())).into(imageCountry)
            }else{
                Utils().fetchSVG(this@ProfilActivity,obj[userModel.country].toString(),imageCountry)
            }
        }catch (e: JSONException) {
            e.printStackTrace()
        }

        // toggle menu

        toggle = ActionBarDrawerToggle(this,binding.drawerLayout,R.string.open,R.string.close)
        binding.drawerLayout.addDrawerListener(toggle) // add le toggle au layout
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.item_profil -> startActivity(Intent(this,ProfilActivity::class.java))
                R.id.item_leaderboard -> StatsRepository().updateDate { startActivity(Intent(this,LeaderBoardActivity::class.java)) }
                R.id.item_home -> startActivity(Intent(this,MainActivity::class.java))
                //R.id.item_logout ->
                //R.id.item_rate ->
                //R.id.item_settings ->
                //R.id.item_share ->
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
    open fun loadJSONFromAsset(): String {
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