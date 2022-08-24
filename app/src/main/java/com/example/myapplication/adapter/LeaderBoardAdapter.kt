package com.example.myapplication.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.LeaderBoardActivity
import com.example.myapplication.MainActivity
import com.example.myapplication.objets.ProfilModel
import com.example.myapplication.R
import com.example.myapplication.databinding.LeaderboardBinding
import com.example.myapplication.databinding.LeaderboardVerticalProfilesBinding

class LeaderBoardAdapter(
    private val listPlayer : List<ProfilModel>
    ) : RecyclerView.Adapter<LeaderBoardAdapter.ViewHolder>(){

    //boite pour ranger tout les composants à controler

    class ViewHolder(val binding : LeaderboardVerticalProfilesBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LeaderboardVerticalProfilesBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentProfil = listPlayer[position]
        if(position == 0 ){
             // si le joueur est le premier on lui donne une couleur de fond différente
            holder.binding.itemLeaderboard.background = getDrawable(holder.binding.root.context,R.drawable.backfirst)
        }
        if(position == 1 ){
            holder.binding.itemLeaderboard.background = getDrawable(holder.binding.root.context,
                R.drawable.backsecond
            )  // si le joueur est le premier on lui donne une couleur de fond différente
        }
        if(position == 2 ){
            holder.binding.itemLeaderboard.background = getDrawable(holder.binding.root.context,
                R.drawable.backthird
            )  // si le joueur est le premier on lui donne une couleur de fond différente
        }

        // récuperer l'image à partir de son lien avec la librairie glide
        // le context contient toutes les informations de l'appli
        Glide.with(holder.binding.root).load(Uri.parse(currentProfil.imageAvatarUrl)).into(holder.binding.imageItem)

        // modif les valeurs de base par les valeurs du profil
        holder.binding.playerLeaderboardPosition.text = (position + 1).toString() + "."
        holder.binding.playerName.text = currentProfil.name
        holder.binding.playerStats.text = currentProfil.bestGame.toString() + " mots/minutes"

    }

    override fun getItemCount(): Int = listPlayer.size


}