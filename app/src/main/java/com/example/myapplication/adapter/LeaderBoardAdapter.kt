package com.example.myapplication.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.BaseDeDonnées.StatsRepository
import com.example.myapplication.LeaderBoardActivity
import com.example.myapplication.MainActivity
import com.example.myapplication.objets.ProfilModel
import com.example.myapplication.R
import com.example.myapplication.databinding.LeaderboardBinding
import com.example.myapplication.databinding.LeaderboardVerticalProfilesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class LeaderBoardAdapter(
    private val context: LeaderBoardActivity,
    private val listPlayer : List<ProfilModel>,
    private val layoutId : Int
    ) : RecyclerView.Adapter<LeaderBoardAdapter.ViewHolder>(){


    private lateinit var databaseRef : DatabaseReference
    private lateinit var firebaseAuth : FirebaseAuth

    //boite pour ranger tout les composants à controler

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val profilImage = view.findViewById<ImageView>(R.id.image_item)
        val profilName = view.findViewById<TextView>(R.id.player_name)
        val profilBestGame = view.findViewById<TextView>(R.id.player_stats)
        val profilRank = view.findViewById<TextView>(R.id.player_leaderboard_position)
        val itemLeaderboard = view.findViewById<ConstraintLayout>(R.id.item_leaderboard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutId,parent,false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentProfil = listPlayer[position]
        if(position == 0 ){
             // si le joueur est le premier on lui donne une couleur de fond différente
            holder.itemLeaderboard.background = getDrawable(context,R.drawable.backfirst)
        }
        if(position == 1 ){
            holder.itemLeaderboard.background = getDrawable(context,
                R.drawable.backsecond
            )  // si le joueur est le second on lui donne une couleur de fond différente
        }
        if(position == 2 ){
            holder.itemLeaderboard.background = getDrawable(context,
                R.drawable.backthird
            )  // si le joueur est le troisième on lui donne une couleur de fond différente
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
                        }
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })

        // récuperer l'image à partir de son lien avec la librairie glide
        // le context contient toutes les informations de l'appli
        // modif les valeurs de base par les valeurs du profil

        holder.profilBestGame?.text = currentProfil.bestGame.toString() + " mots/minutes"
        holder.profilRank?.text = (position + 1).toString() + "."

    }

    override fun getItemCount(): Int = listPlayer.size


}