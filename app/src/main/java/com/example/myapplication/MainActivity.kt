package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.myapplication.BaseDeDonnées.StatsRepository
import com.example.myapplication.databinding.GameBinding
import com.example.myapplication.databinding.LoginBinding
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.io.FileReader
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.random.Random
import com.opencsv.CSVReader
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    // Timer variables
    enum class TimerState{
        Stopped,Paused,Running
    }
    private lateinit var timer : CountDownTimer
    private var timerLenghtSeconds = 0L
    private var timerState = TimerState.Stopped
    private var secondsRemaining = 0L

    private lateinit var binding: GameBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        binding = GameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        scannerEtAjout()

        binding.leaderboardButton.setOnClickListener {
            val repo = StatsRepository()
            repo.updateDate {
                val intent = Intent(this,LeaderBoardActivity::class.java)
                startActivity(intent)
            }
        }

    }

    val sauvegarde = mutableListOf<String>()

    @SuppressLint("SetTextI18n")
    fun scannerEtAjout(){
        sauvegarde.clear()

        val listeMots = mutableListOf<String>()

        val minput = InputStreamReader(assets.open("listWords.csv"))
        val reader = BufferedReader(minput)
        var line : String?
        var displayData = ""
        while (reader.readLine().also { line = it } != null){
            listeMots.add(line!!)
        }
        for (j in 0 until 200){
            val rand = Random.nextInt(0, listeMots.size)
            sauvegarde.add(listeMots[rand])
        }
        for(word in sauvegarde){
            displayData += "$word "
        }
        binding.textGame.setText(displayData)
    }
}


