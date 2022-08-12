package com.example.myapplication.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.LeaderBoardActivity
import com.example.myapplication.MainActivity
import com.example.myapplication.objets.ProfilModel
import com.example.myapplication.R

class LeaderBoardAdapter(
    private val context : LeaderBoardActivity,
    private val listPlayer : List<ProfilModel>,
    private val layoutId : Int
    ) : RecyclerView.Adapter<LeaderBoardAdapter.ViewHolder>(){

    //boite pour ranger tout les composants à controler

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val profilImage = view.findViewById<ImageView>(R.id.image_item)
        val profilName = view.findViewById<TextView>(R.id.player_name)
        val profilBestGame = view.findViewById<TextView>(R.id.player_stats)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutId,parent,false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentProfil = listPlayer[position]

        // récuperer l'image à partir de son lien avec la librairie glide
        // le context contient toutes les informations de l'appli
        Glide.with(context).load(Uri.parse(currentProfil.imageAvatarUrl)).into(holder.profilImage)

        // modif les valeurs de base par les valeurs du profil
        holder.profilName?.text = currentProfil.name
        holder.profilBestGame?.text = currentProfil.bestGame.toString() + " mots/minutes"

    }

    override fun getItemCount(): Int = listPlayer.size


}