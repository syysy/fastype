package com.example.myapplication.BaseDeDonnées

import com.example.myapplication.BaseDeDonnées.StatsRepository.Singleton.databaseRef
import com.example.myapplication.BaseDeDonnées.StatsRepository.Singleton.listPlayer
import com.example.myapplication.objets.ProfilModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class StatsRepository {

    // pour éviter de recréer une liste vide à chaque appel de la class StatsRepository
    object Singleton{
        // se connecter à la dataBase Firebase
        val databaseRef = FirebaseDatabase.getInstance().getReference("players")

        // liste des users
        val listPlayer = mutableListOf<ProfilModel>()
    }
    
    fun updateDate(callback:() -> Unit){
        listPlayer.clear()
        // absorber les données
        databaseRef.addListenerForSingleValueEvent( object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                // recolter la liste
                for(i in p0.children){
                    val user = i.getValue(ProfilModel::class.java)
                    if (user != null){
                        if (listPlayer.size == 0){
                            listPlayer.add(user)
                        }
                        if (user in listPlayer){ // verif si un user est déjà présent de le classement
                                listPlayer[listPlayer.indexOf(user)].bestGame = user.bestGame
                        }else{
                            listPlayer.add(user)
                        }
                    }
                }
                listPlayer.sortByDescending { ProfilModel -> ProfilModel.bestGame }
                callback()
            }
            override fun onCancelled(p0: DatabaseError) {}

        })



    }




}