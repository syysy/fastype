package com.example.myapplication.objets

class ProfilModel (
    public var name : String,
    private var email : String,
    private var moyenne : Int,
    public var bestGame: Int = 0,
    public var imageAvatarUrl : String = "https://cdn.pixabay.com/photo/2013/07/13/10/44/man-157699_960_720.png",
    private var numberGamePlayed : Int = 0,
    private var country : String = "Inconnu"
        ) {


    // --- changer les attributs
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
}