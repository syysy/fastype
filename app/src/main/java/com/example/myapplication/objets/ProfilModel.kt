package com.example.myapplication.objets

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

}