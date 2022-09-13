package com.example.myapplication.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.*
import com.example.myapplication.BaseDeDonnées.StatsRepository
import com.example.myapplication.R
import com.example.myapplication.objets.ProfilModel
import com.example.myapplication.databinding.LeaderboardBinding
import com.example.myapplication.databinding.LeaderboardVerticalProfilesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.nio.channels.AsynchronousFileChannel.open
import java.nio.charset.Charset

class LeaderBoardAdapter(
    private val context: LeaderBoardActivity,
    private val listPlayer : List<ProfilModel>,
    private val layoutId : Int
    ) : RecyclerView.Adapter<LeaderBoardAdapter.ViewHolder>(){


    private lateinit var databaseRef : DatabaseReference
    private lateinit var firebaseAuth : FirebaseAuth

    // boite pour ranger tout les composants à controler

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val profilImage = view.findViewById<ImageView>(R.id.image_item)
        val profilName = view.findViewById<TextView>(R.id.player_name)
        val profilBestGame = view.findViewById<TextView>(R.id.player_stats)
        val profilRank = view.findViewById<TextView>(R.id.player_leaderboard_position)
        var profilCountry = view.findViewById<ImageView>(R.id.player_country_leaderboard)
        val itemLeaderboard = view.findViewById<ConstraintLayout>(R.id.item_leaderboard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentProfil = listPlayer[position]
        if (position == 0) {
            holder.itemLeaderboard.background = getDrawable(context, R.drawable.backfirst)
            // si le joueur est le premier on lui donne une couleur de fond différente
        }
        if (position == 1) {
            holder.itemLeaderboard.background = getDrawable(context, R.drawable.backsecond)
            // si le joueur est le second on lui donne une couleur de fond différente
        }
        if (position == 2) {
            holder.itemLeaderboard.background = getDrawable(context, R.drawable.backthird)
            // si le joueur est le troisième on lui donne une couleur de fond différente
        }

        holder.itemLeaderboard.setOnClickListener {
            this.viewProfil(currentProfil.email)
        }

        // mettre à jour les players en fonction des potentiels changements de nom/ image de profils

        firebaseAuth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().getReference("players")
        databaseRef.addListenerForSingleValueEvent( object :
            ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                // recolter la liste
                for (i in p0.children) {
                    val user = i.getValue(ProfilModel::class.java)
                    if (user != null) {
                        if (user.email == currentProfil.email){
                            // si l'email du joueur est le même que celui de la liste
                            // on met à jour les données
                            holder.profilName.text = user.name
                            Glide.with(context).load(user.imageAvatarUrl).into(holder.profilImage)
                            try {
                                val obj = JSONObject(OpenAsset().loadJsonFromRaw(context))
                                if (user.country == "Unknown"){
                                    Glide.with(context).load(Uri.parse(obj[user.country].toString())).into(holder.profilCountry)
                                } else {
                                    ProfilActivity.Utils().fetchSVG(context, obj[user.country].toString(),holder.profilCountry)
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                            break
                        }
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })

        // récuperer l'image à partir de son lien avec la librairie glide
        // le context contient toutes les informations de l'appli
        // modif les valeurs de base par les valeurs du profil

        val editRessources = EditRessources(context)
        var deviceLanguage : String
        try {
            deviceLanguage = editRessources.loadEditableJsonFile("app_config.json")["language"].toString()
            LocaleHelper.setLocale(context, deviceLanguage)
        } catch (e: JSONException) {
            editRessources.writeJsonFile("app_config.json", JSONObject().put("language", "en"))
            deviceLanguage = "en"
        }

        when(deviceLanguage){
            "fr" -> {
                holder.profilBestGame?.text = currentProfil.bestGame.toString() + " mots/min"
            }
            else -> {
                holder.profilBestGame?.text = currentProfil.bestGame.toString() + " words/min"
            }
        }

        holder.profilRank?.text = (position + 1).toString() + "."

        // si il y a égalité de score on affiche le même rang
        if (position > 0) {
            if (listPlayer[position].bestGame == listPlayer[position - 1].bestGame) {
                holder.profilRank?.text = (position).toString() + "."
            }
        }

    }

    override fun getItemCount(): Int = listPlayer.size

    class OpenAsset : AppCompatActivity(){
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
        }

        fun loadJsonFromRaw(context: Context): String{
            val jsonData = context.resources.openRawResource(R.raw.country).bufferedReader().use { it.readText() }
            return jsonData
        }
    }

    fun viewProfil(playerEmail: String) {
        context.startActivity(Intent(context, ProfliViewerActivity::class.java).putExtra("playerEmail", playerEmail).putExtra("user",firebaseAuth.currentUser))
    }

}
