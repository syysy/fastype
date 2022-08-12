package com.example.myapplication.objets

class ProfilModel (
    var name : String = "",
    var email : String = "",
    var moyenne : Int = 0,
    var bestGame: Int = 0,
    var imageAvatarUrl : String = "https://cdn.pixabay.com/photo/2013/07/13/10/44/man-157699_960_720.png",
    var numberGamePlayed : Int = 0,
    var country : String = "Unknown"
){
    public fun changeName(newName: String) {
        this.name = newName
    }

    public fun changeAvatarUrl(url: String) {
        this.imageAvatarUrl = url
    }

    public fun changeCountry(country: String) {
        this.country = country
    }

    // --- mise à jour des données a chaque partie
    public fun newGame(score: Int) {
        this.moyenne = ((this.moyenne * this.numberGamePlayed) + score ) / this.numberGamePlayed++
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