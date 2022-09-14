package com.example.myapplication.objets

import com.example.myapplication.BaseDeDonnées.StatsRepository.Singleton.databaseRef
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.JsonObject

class ProfilModel (
    var name : String = "",
    var email : String = "",
    var moyenne : Double = 0.0,
    var bestGame: Int = 0,
    var imageAvatarUrl : String = "https://cdn.pixabay.com/photo/2013/07/13/10/44/man-157699_960_720.png",
    var numberGamePlayed : Int = 0,
    var country : String = "Unknown"
) {
    /* mise à jour des données a chaque partie */
    fun newGame(score: Int, moyenneOld: Double) {
        this.numberGamePlayed++
        this.moyenne = ((moyenneOld * (this.numberGamePlayed-1)) + score ) / this.numberGamePlayed
        this.moyenne = Math.round(this.moyenne * 100.0) / 100.0 // deux nombres max apres la virgule
        if (score > this.bestGame) {
            this.bestGame = score
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other === null || other !is ProfilModel) return false
        if (other === this) return true
        return other.email == this.email
    }

    fun instancierProfil(uid: String): ProfilModel {
        val firebaseAuth = FirebaseAuth.getInstance()
        val databaseRef = FirebaseDatabase.getInstance().getReference("players")
        databaseRef.child(uid).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val data = it.result!!.value as HashMap<*, *>
                this.name = data["name"] as String
                this.email = data["email"] as String
                this.moyenne = data["moyenne"] as Double
                this.bestGame = data["bestGame"] as Int
                this.imageAvatarUrl = data["imageAvatarUrl"] as String
                this.numberGamePlayed = data["numberGamePlayed"] as Int
                this.country = data["country"] as String
            }
        }.continueWith {
            return@continueWith this
        }
        return this

    }

}